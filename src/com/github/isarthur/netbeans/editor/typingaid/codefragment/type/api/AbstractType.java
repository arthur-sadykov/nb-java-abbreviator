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
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
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
        ExpressionTree initializer;
        switch (request.getCurrentKind()) {
            case AND:
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
            case DIVIDE:
            case EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LEFT_SHIFT:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
            case MINUS:
            case MULTIPLY:
            case NOT_EQUAL_TO:
            case OR:
            case PLUS:
            case REMAINDER:
            case RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT:
            case XOR:
                BinaryTree binaryTree = (BinaryTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(binaryTree.getRightOperand(), request);
            case AND_ASSIGNMENT:
            case DIVIDE_ASSIGNMENT:
            case LEFT_SHIFT_ASSIGNMENT:
            case MINUS_ASSIGNMENT:
            case MULTIPLY_ASSIGNMENT:
            case OR_ASSIGNMENT:
            case PLUS_ASSIGNMENT:
            case REMAINDER_ASSIGNMENT:
            case RIGHT_SHIFT_ASSIGNMENT:
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            case XOR_ASSIGNMENT:
                CompoundAssignmentTree compoundAssignmentTree = (CompoundAssignmentTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(compoundAssignmentTree.getExpression(), request);
            case BITWISE_COMPLEMENT:
            case LOGICAL_COMPLEMENT:
            case POSTFIX_DECREMENT:
            case POSTFIX_INCREMENT:
            case PREFIX_DECREMENT:
            case PREFIX_INCREMENT:
            case UNARY_MINUS:
            case UNARY_PLUS:
                UnaryTree unaryTree = (UnaryTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(unaryTree.getExpression(), request);
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
                ReturnTree returnTree = (ReturnTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(returnTree.getExpression(), request);
            case VARIABLE:
                VariableTree variableTree = (VariableTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(variableTree.getInitializer(), request);
            default:
                return JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
        }
    }

    private Tree getNewClassOrTypeCastTree(ExpressionTree expression, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TokenSequence<?> tokensSequence = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        tokensSequence.move(abbreviation.getStartOffset());
        while (tokensSequence.moveNext()) {
            Token<?> token = tokensSequence.token();
            if (token.id() == JavaTokenId.WHITESPACE) {
                continue;
            }
            if (token.id() == JavaTokenId.SEMICOLON) {
                return JavaSourceMaker.makeNewClassTree(getIdentifier(), request);
            } else {
                return JavaSourceMaker.makeTypeCastTree(
                        JavaSourceMaker.makeQualIdentTree(toString(), request),
                        expression,
                        request);
            }
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
