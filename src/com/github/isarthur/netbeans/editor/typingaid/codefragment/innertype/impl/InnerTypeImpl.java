/*
 * Copyright 2021 Arthur Sadykov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.INNER_TYPE;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.api.InnerType;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class InnerTypeImpl implements InnerType {

    private final ElementHandle<TypeElement> scope;
    private final ElementHandle<TypeElement> identifier;

    public InnerTypeImpl(ElementHandle<TypeElement> scope, ElementHandle<TypeElement> identifier) {
        this.scope = scope;
        this.identifier = identifier;
    }

    @Override
    public ElementHandle<TypeElement> getScope() {
        return scope;
    }

    @Override
    public ElementHandle<TypeElement> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        String scopeAbbreviation = StringUtilities.getElementAbbreviation(scope.getBinaryName());
        String identifierAbbreviation = StringUtilities.getElementAbbreviation(identifier.getBinaryName());
        return abbreviation.equals(scopeAbbreviation + "." + identifierAbbreviation); //NOI18N
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        DeclaredType declaredType = types.getDeclaredType(identifier.resolve(copy));
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokensSequence;
        ClassTree classTree;
        switch (request.getCurrentKind()) {
            case BLOCK:
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(declaredType, request),
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        JavaSourceMaker.makeNewClassOrEnumAccessTree(
                                scope.resolve(copy), identifier.resolve(copy), request),
                        request);
            case CLASS:
                classTree = (ClassTree) request.getCurrentTree();
                if (JavaSourceUtilities.isInsideExtendsTreeSpan(request)) {
                    return JavaSourceMaker.makeTypeTree(identifier.getQualifiedName(), request);
                } else if (JavaSourceUtilities.isInsideClassOrInterfaceBodySpan(classTree, request)) {
                    if (!JavaSourceUtilities.isMethodSection(classTree, request)) {
                        return JavaSourceMaker.makeVariableTree(
                                JavaSourceMaker.makeModifiersTree(Collections.singleton(Modifier.PRIVATE), request),
                                JavaSourceUtilities.getVariableName(declaredType, request),
                                JavaSourceMaker.makeTypeTree(toString(), request),
                                null,
                                request);
                    } else {
                        return JavaSourceMaker.makeMethodTree(toString(), request);
                    }
                } else {
                    throw new RuntimeException("Wrong position for type completion in class declaration."); //NOI18N
                }
            case ENUM:
                classTree = (ClassTree) request.getCurrentTree();
                if (!JavaSourceUtilities.isMethodSection(classTree, request)) {
                    return JavaSourceMaker.makeVariableTree(
                            JavaSourceMaker.makeModifiersTree(Collections.singleton(Modifier.PRIVATE), request),
                            JavaSourceUtilities.getVariableName(declaredType, request),
                            JavaSourceMaker.makeTypeTree(toString(), request),
                            null,
                            request);
                } else {
                    return JavaSourceMaker.makeMethodTree(toString(), request);
                }
            case INTERFACE:
                return JavaSourceMaker.makeMethodTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        "method", //NOI18N
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        null,
                        request);
            case METHOD:
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(declaredType, request),
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        null,
                        request);
            case PARAMETERIZED_TYPE:
                return JavaSourceMaker.makeTypeTree(toString(), request);
            case RETURN:
                tokensSequence = copy.getTokenHierarchy().tokenSequence();
                tokensSequence.move(abbreviation.getStartOffset());
                while (tokensSequence.moveNext()) {
                    Token<?> token = tokensSequence.token();
                    if (token.id() == JavaTokenId.WHITESPACE) {
                        continue;
                    }
                    if (token.id() == JavaTokenId.SEMICOLON) {
                        return JavaSourceMaker.makeNewClassOrEnumAccessTree(
                                scope.resolve(copy), identifier.resolve(copy), request);
                    } else {
                        ReturnTree returnTree = (ReturnTree) request.getCurrentTree();
                        return JavaSourceMaker.makeTypeCastTree(
                                JavaSourceMaker.makeQualIdentTree(toString(), request),
                                returnTree.getExpression(),
                                request);
                    }
                }
                break;
            case VARIABLE:
                tokensSequence = copy.getTokenHierarchy().tokenSequence();
                tokensSequence.move(abbreviation.getStartOffset());
                while (tokensSequence.moveNext()) {
                    Token<?> token = tokensSequence.token();
                    if (token.id() == JavaTokenId.WHITESPACE) {
                        continue;
                    }
                    if (token.id() == JavaTokenId.SEMICOLON) {
                        return JavaSourceMaker.makeNewClassOrEnumAccessTree(
                                scope.resolve(copy), identifier.resolve(copy), request);
                    } else {
                        VariableTree variableTree = (VariableTree) request.getCurrentTree();
                        return JavaSourceMaker.makeTypeCastTree(
                                JavaSourceMaker.makeQualIdentTree(toString(), request),
                                variableTree.getInitializer(),
                                request);
                    }
                }
                break;
            default:
                return JavaSourceMaker.makeNewClassOrEnumAccessTree(
                        scope.resolve(copy), identifier.resolve(copy), request);
        }
        return null;
    }

    @Override
    public Kind getKind() {
        return INNER_TYPE;
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public String toString() {
        return scope.getQualifiedName() + "." + identifier.getBinaryName(); //NOI18N
    }
}
