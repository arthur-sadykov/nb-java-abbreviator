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
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.Collections;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractType implements Type, Comparable<AbstractType> {

    protected ElementHandle<TypeElement> identifier;
    private final int typeParametersCount;

    protected AbstractType(ElementHandle<TypeElement> identifier, int typeParametersCount) {
        this.identifier = identifier;
        this.typeParametersCount = typeParametersCount;
    }

    @Override
    public ElementHandle<TypeElement> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(toString()).equals(abbreviation);
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
        DeclaredType declaredType = types.getDeclaredType(identifier.resolve(copy));
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
                TokenHierarchy< ?> tokenHierarchy = copy.getTokenHierarchy();
                TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
                Abbreviation abbreviation = request.getAbbreviation();
                tokenSequence.move(abbreviation.getStartOffset());
                while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                Token<?> token = tokenSequence.token();
                if (token != null && token.id() == JavaTokenId.EQ) {
                    while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.SEMICOLON) {
                    }
                    token = tokenSequence.token();
                    if (token != null && token.id() == JavaTokenId.SEMICOLON) {
                        TreeUtilities treeUtilities = copy.getTreeUtilities();
                        TreePath treePath = treeUtilities.pathFor(tokenSequence.offset());
                        AssignmentTree assignmentTree = (AssignmentTree) treePath.getLeaf();
                        treePath = TreePath.getPath(treePath, assignmentTree.getExpression());
                        Trees trees = copy.getTrees();
                        TypeMirror typeMirror = trees.getTypeMirror(treePath);
                        TypeElement typeElement = identifier.resolve(copy);
                        if (typeElement == null) {
                            return null;
                        }
                        TypeMirror currentTypeMirror = typeElement.asType();
                        if (typeMirror != null && types.isAssignable(typeMirror, currentTypeMirror)) {
                            return JavaSourceMaker.makeVariableTree(
                                    JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                                    JavaSourceUtilities.getVariableName(currentTypeMirror, request),
                                    JavaSourceMaker.makeTypeTree(toString(), request),
                                    assignmentTree.getExpression(),
                                    request);
                        }
                    }
                } else {
                    NewClassTree newClassTree = JavaSourceMaker.makeNewClassTree(identifier.resolve(copy), request);
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
                }
                break;
            case CATCH:
                return JavaSourceMaker.makeTypeTree(declaredType, request);
            case CLASS:
            case ENUM:
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
            case INSTANCE_OF:
            case PARAMETERIZED_TYPE:
                return JavaSourceMaker.makeTypeTree(toString(), request);
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
            case RETURN:
                ReturnTree returnTree = (ReturnTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(returnTree.getExpression(), request);
            case VARIABLE:
                VariableTree variableTree = (VariableTree) request.getCurrentTree();
                return getNewClassOrTypeCastTree(variableTree.getInitializer(), request);
            default:
                return JavaSourceMaker.makeNewClassTree(identifier.resolve(copy), request);
        }
        return null;
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
                return JavaSourceMaker.makeNewClassTree(identifier.resolve(copy), request);
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
        if (typeParametersCount == 0) {
            return identifier.getQualifiedName();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < typeParametersCount; i++) {
                stringBuilder.append("String, "); //NOI18N
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            return identifier.getQualifiedName() + "<" + stringBuilder.toString() + ">"; //NOI18N
        }
    }
}
