/*
 * Copyright 2020 Arthur Sadykov.
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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.type.api;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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
public abstract class AbstractType implements Type, Comparable<AbstractType> {

    protected TypeElement identifier;

    protected AbstractType(TypeElement identifier) {
        this.identifier = identifier;
    }

    @Override
    public TypeElement getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(identifier.getSimpleName().toString()).equals(abbreviation);
    }

    @Override
    public int compareTo(AbstractType other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        DeclaredType declaredType = types.getDeclaredType(getIdentifier());
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokensSequence;
        ExpressionTree initializer;
        switch (request.getCurrentKind()) {
            case BLOCK:
                NewClassTree newClassTree = JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
                if (newClassTree != null) {
                    initializer = newClassTree;
                } else {
                    initializer = JavaSourceMaker.makeLiteralTree(null, request);
                }
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(declaredType, request),
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        initializer,
                        request);
            case CATCH:
                return JavaSourceMaker.makeTypeTree(declaredType, request);
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
                        return JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
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
                        return JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
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
                return JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
        }
        return null;
    }

    @Override
    public String toString() {
        List<? extends TypeParameterElement> typeParameters = identifier.getTypeParameters();
        if (typeParameters.isEmpty()) {
            return identifier.getQualifiedName().toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            typeParameters.forEach(typeParameter -> stringBuilder.append("String, ")); //NOI18N
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            return identifier.getQualifiedName().toString() + "<" + stringBuilder.toString() + ">"; //NOI18N
        }
    }
}
