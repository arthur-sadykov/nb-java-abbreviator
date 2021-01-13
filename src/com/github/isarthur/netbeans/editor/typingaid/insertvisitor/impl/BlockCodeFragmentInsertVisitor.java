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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThisKeyword;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.AbstractCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import static com.sun.source.tree.Tree.Kind.METHOD;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class BlockCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<?> tokenSequence = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        Token<?> token;
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case STRICTFP_MODIFIER:
                tokenSequence.move(abbreviation.getStartOffset());
                while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                token = tokenSequence.token();
                if (token != null && JavaSourceUtilities.isModifier(token.id())) {
                    ClassTree originalTree = (ClassTree) request.getCurrentTree();
                    return originalTree.getModifiers();
                }
                tokenSequence.move(abbreviation.getStartOffset());
                while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                TreePath path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(CLASS, ENUM, INTERFACE, METHOD, VARIABLE),
                        treeUtilities.pathFor(tokenSequence.offset()));
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
            case EXTERNAL_TYPE:
            case GLOBAL_TYPE:
            case INTERNAL_TYPE:
                tokenSequence.move(abbreviation.getStartOffset());
                while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                token = tokenSequence.token();
                if (token != null && token.id() == JavaTokenId.EQ) {
                    while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.SEMICOLON) {
                    }
                    token = tokenSequence.token();
                    if (token != null && token.id() == JavaTokenId.SEMICOLON) {
                        TreePath treePath = treeUtilities.pathFor(tokenSequence.offset());
                        treePath = treePath.getParentPath();
                        if (treePath == null) {
                            return null;
                        }
                        Tree tree = treePath.getLeaf();
                        if (tree.getKind() != Tree.Kind.EXPRESSION_STATEMENT) {
                            return null;
                        }
                        return tree;
                    }
                }
                return request.getCurrentTree();
            default:
                return request.getCurrentTree();
        }
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        if (tree == null) {
            return null;
        }
        WorkingCopy copy = request.getWorkingCopy();
        TreeMaker make = copy.getTreeMaker();
        switch (tree.getKind()) {
            case VARIABLE:
                Tree originalTree = getOriginalTree(codeFragment, request);
                if (originalTree == null) {
                    return null;
                }
                if (originalTree.getKind() == Tree.Kind.BLOCK) {
                    return getBlockTree(codeFragment, tree, request);
                }
                return tree;
            case MODIFIERS:
                ModifiersTree originalModifiersTree = (ModifiersTree) getOriginalTree(codeFragment, request);
                ModifiersTree modifiersTree = (ModifiersTree) tree;
                return make.addModifiersModifier(originalModifiersTree, modifiersTree.getFlags().iterator().next());
            default:
                return getBlockTree(codeFragment, tree, request);
        }
    }

    private BlockTree getBlockTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        BlockTree blockTree = (BlockTree) getOriginalTree(codeFragment, request);
        Abbreviation abbreviation = request.getAbbreviation();
        WorkingCopy copy = request.getWorkingCopy();
        int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                abbreviation.getStartOffset(), blockTree.getStatements(), copy);
        if (insertIndex == -1) {
            return null;
        }
        TreeMaker make = copy.getTreeMaker();
        return make.insertBlockStatement(blockTree, insertIndex, (StatementTree) tree);
    }

    @Override
    public void visit(ThisKeyword keyword, CodeCompletionRequest request) {
        BlockTree blockTree = (BlockTree) request.getCurrentTree();
        Abbreviation abbreviation = request.getAbbreviation();
        WorkingCopy copy = request.getWorkingCopy();
        int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                abbreviation.getStartOffset(), blockTree.getStatements(), copy);
        if (insertIndex == -1) {
            return;
        }
        TreeMaker make = copy.getTreeMaker();
        TreePath methodPath = JavaSourceUtilities.getParentPathOfKind(Collections.singleton(METHOD), request);
        if (methodPath == null) {
            return;
        }
        Supplier<Map<VariableElement, VariableElement>> getParametersByFields = () -> {
            Map<VariableElement, VariableElement> parametersByFields = new HashMap<>();
            Trees trees = copy.getTrees();
            Types types = copy.getTypes();
            ExecutableElement method = (ExecutableElement) trees.getElement(methodPath);
            List<? extends VariableElement> parameters = method.getParameters();
            Element enclosingElement = method.getEnclosingElement();
            if (enclosingElement.getKind() == ElementKind.CLASS || enclosingElement.getKind() == ElementKind.ENUM) {
                List<? extends Element> enclosedElements = enclosingElement.getEnclosedElements();
                List<VariableElement> fields = ElementFilter.fieldsIn(enclosedElements);
                OUTER:
                for (VariableElement field : fields) {
                    for (VariableElement parameter : parameters) {
                        if (types.isAssignable(field.asType(), parameter.asType())) {
                            parametersByFields.put(field, parameter);
                            continue OUTER;
                        }
                    }
                }
            }
            return Collections.unmodifiableMap(parametersByFields);
        };
        Map<VariableElement, VariableElement> parametersByFields = getParametersByFields.get();
        if (parametersByFields.isEmpty()) {
            return;
        }
        for (VariableElement field : parametersByFields.keySet()) {
            AssignmentTree assignmentTree =
                    make.Assignment(
                            make.MemberSelect(make.Identifier("this"), field), //NOI18N
                            make.Identifier(parametersByFields.get(field)));
            BlockTree newBlockTree = make.insertBlockStatement(
                    blockTree, insertIndex, make.ExpressionStatement(assignmentTree));
            copy.rewrite(blockTree, newBlockTree);
            blockTree = newBlockTree;
        }
    }
}
