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
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class CompilationUnitCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case PUBLIC_MODIFIER:
            case STRICTFP_MODIFIER:
                WorkingCopy copy = request.getWorkingCopy();
                TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
                Abbreviation abbreviation = request.getAbbreviation();
                tokens.move(abbreviation.getStartOffset());
                while (tokens.moveNext() && tokens.token().id() != JavaTokenId.WHITESPACE) {
                }
                while (tokens.moveNext() && tokens.token().id() != JavaTokenId.WHITESPACE) {
                }
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath path = treeUtilities.getPathElementOfKind(
                        TreeUtilities.CLASS_TREE_KINDS,
                        treeUtilities.pathFor(tokens.offset()));
                if (path == null) {
                    return null;
                }
                ClassTree classTree = (ClassTree) path.getLeaf();
                return classTree.getModifiers();
            default:
                return request.getCurrentTree();
        }
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Abbreviation abbreviation = request.getAbbreviation();
        int insertIndex;
        CompilationUnitTree originalTree;
        switch (tree.getKind()) {
            case MODIFIERS:
                ModifiersTree originalModifiersTree = (ModifiersTree) getOriginalTree(codeFragment, request);
                ModifiersTree modifiersTree = (ModifiersTree) tree;
                return JavaSourceMaker.makeModifiersTree(
                        originalModifiersTree, modifiersTree.getFlags().iterator().next(), request);
            case IMPORT:
                originalTree = (CompilationUnitTree) getOriginalTree(codeFragment, request);
                insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                        abbreviation.getStartOffset(), originalTree.getImports(), copy);
                if (insertIndex == -1) {
                    return null;
                }
                return JavaSourceMaker.makeCompilationUnitTree(originalTree, insertIndex, (ImportTree) tree, request);
            default:
                originalTree = (CompilationUnitTree) getOriginalTree(codeFragment, request);
                insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                        abbreviation.getStartOffset(), originalTree.getTypeDecls(), copy);
                if (insertIndex == -1) {
                    return null;
                }
                return JavaSourceMaker.makeCompilationUnitTree(originalTree, insertIndex, tree, request);
        }
    }
}
