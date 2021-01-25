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
package com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.AbstractCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import static com.sun.source.tree.Tree.Kind.METHOD;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class ClassCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case NATIVE_MODIFIER:
            case PRIVATE_MODIFIER:
            case PROTECTED_MODIFIER:
            case PUBLIC_MODIFIER:
            case STATIC_MODIFIER:
            case STRICTFP_MODIFIER:
            case TRANSIENT_MODIFIER:
            case VOLATILE_MODIFIER:
                WorkingCopy copy = request.getWorkingCopy();
                TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
                Abbreviation abbreviation = request.getAbbreviation();
                tokens.move(abbreviation.getStartOffset());
                while (tokens.movePrevious() && tokens.token().id() == JavaTokenId.WHITESPACE) {
                }
                Token<?> token = tokens.token();
                if (token != null && JavaSourceUtilities.isModifier(token.id())) {
                    ClassTree originalTree = (ClassTree) request.getCurrentTree();
                    return originalTree.getModifiers();
                }
                tokens.move(abbreviation.getStartOffset());
                while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
                }
                while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
                }
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(CLASS, ENUM, INTERFACE, METHOD, VARIABLE),
                        treeUtilities.pathFor(tokens.offset()));
                if (path == null) {
                    return null;
                }
                switch (path.getLeaf().getKind()) {
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree classTree = (ClassTree) path.getLeaf();
                        return classTree.getModifiers();
                    case METHOD:
                        MethodTree methodTree = (MethodTree) path.getLeaf();
                        return methodTree.getModifiers();
                    case VARIABLE:
                        VariableTree variableTree = (VariableTree) path.getLeaf();
                        return variableTree.getModifiers();
                }
                return request.getCurrentTree();
            default:
                return request.getCurrentTree();
        }
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        ClassTree originalTree;
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case NATIVE_MODIFIER:
            case PRIVATE_MODIFIER:
            case PROTECTED_MODIFIER:
            case PUBLIC_MODIFIER:
            case STATIC_MODIFIER:
            case STRICTFP_MODIFIER:
            case TRANSIENT_MODIFIER:
            case VOLATILE_MODIFIER:
                ModifiersTree originalModifiersTree = (ModifiersTree) getOriginalTree(codeFragment, request);
                ModifiersTree modifiersTree = (ModifiersTree) tree;
                return JavaSourceMaker.makeModifiersTree(
                        originalModifiersTree, modifiersTree.getFlags().iterator().next(), request);
            case EXTENDS_KEYWORD:
                originalTree = (ClassTree) getOriginalTree(codeFragment, request);
                return JavaSourceMaker.makeClassTree(originalTree, (ExpressionTree) tree, request);
            case INNER_TYPE:
            case TYPE:
                originalTree = (ClassTree) getOriginalTree(codeFragment, request);
                if (JavaSourceUtilities.isInsideExtendsTreeSpan(request)) {
                    return JavaSourceMaker.makeClassTree(originalTree, (ExpressionTree) tree, request);
                } else if (JavaSourceUtilities.isInsideImplementsTreeSpan(request)) {
                    return JavaSourceMaker.makeClassTree(originalTree, tree, request);
                } else if (JavaSourceUtilities.isInsideClassEnumOrInterfaceBodySpan(originalTree, request)) {
                    Abbreviation abbreviation = request.getAbbreviation();
                    WorkingCopy copy = request.getWorkingCopy();
                    int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                            abbreviation.getStartOffset(), originalTree.getMembers(), copy);
                    return JavaSourceMaker.makeClassEnumOrInterfaceTree(originalTree, insertIndex, tree, request);
                } else {
                    throw new RuntimeException("Wrong position for type completion in class declaration."); //NOI18N
                }

            case IMPLEMENTS_KEYWORD:
                originalTree = (ClassTree) getOriginalTree(codeFragment, request);
                return JavaSourceMaker.makeClassTree(originalTree, tree, request);
            default:
                originalTree = (ClassTree) getOriginalTree(codeFragment, request);
                Abbreviation abbreviation = request.getAbbreviation();
                WorkingCopy copy = request.getWorkingCopy();
                int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                        abbreviation.getStartOffset(), originalTree.getMembers(), copy);
                return JavaSourceMaker.makeClassEnumOrInterfaceTree(originalTree, insertIndex, tree, request);
        }
    }
}
