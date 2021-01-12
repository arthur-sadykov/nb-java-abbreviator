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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.api;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
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
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractInnerType implements InnerType {

    private TypeElement scope;
    private TypeElement identifier;

    protected AbstractInnerType(TypeElement scope, TypeElement identifier) {
        this.scope = scope;
        this.identifier = identifier;
    }

    @Override
    public TypeElement getScope() {
        return scope;
    }

    @Override
    public TypeElement getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        String scopeAbbreviation = StringUtilities.getElementAbbreviation(scope.getSimpleName().toString());
        String identifierAbbreviation = StringUtilities.getElementAbbreviation(identifier.getSimpleName().toString());
        return abbreviation.equals(scopeAbbreviation + "." + identifierAbbreviation); //NOI18N
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        DeclaredType declaredType = types.getDeclaredType(getIdentifier());
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokensSequence;
        switch (request.getCurrentKind()) {
            case BLOCK:
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(declaredType, request),
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        JavaSourceMaker.makeNewClassOrEnumAccessTree(scope, identifier, request),
                        request);
            case CLASS:
            case ENUM:
            case INTERFACE:
                ClassTree classTree = (ClassTree) request.getCurrentTree();
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
                        return JavaSourceMaker.makeNewClassOrEnumAccessTree(scope, identifier, request);
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
                        return JavaSourceMaker.makeNewClassOrEnumAccessTree(scope, identifier, request);
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
                return JavaSourceMaker.makeNewClassOrEnumAccessTree(scope, identifier, request);
        }
        return null;
    }

    @Override
    public String toString() {
        return scope.getSimpleName() + "." + identifier.getSimpleName(); //NOI18N
    }
}
