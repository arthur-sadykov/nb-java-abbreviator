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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.ModifierCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class BlockModifierCollector extends ModifierCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        tokens.move(abbreviation.getStartOffset());
        while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        TreePath path = treeUtilities.getPathElementOfKind(
                EnumSet.of(Tree.Kind.VARIABLE, Tree.Kind.CLASS),
                treeUtilities.pathFor(tokens.offset()));
        if (path != null) {
            switch (path.getLeaf().getKind()) {
                case CLASS:
                    ClassTree classTree = (ClassTree) path.getLeaf();
                    tokens = treeUtilities.tokensFor(classTree);
                    tokens.moveStart();
                    while (tokens.moveNext() && tokens.token().id() != JavaTokenId.LBRACE) {
                    }
                    int[] classSpan = treeUtilities.findBodySpan(classTree);
                    if (abbreviation.getStartOffset() < classSpan[0]
                            || (abbreviation.getStartOffset() >= classSpan[0]
                            && abbreviation.getStartOffset() < tokens.offset())) {
                        ModifiersTree modifiersTree = classTree.getModifiers();
                        collectMethodLocalInnerClassModifiers(modifiersTree, request);
                    }
                    break;
                case VARIABLE:
                    VariableTree variable = (VariableTree) path.getLeaf();
                    ModifiersTree modifiersTree = variable.getModifiers();
                    collectVariableModifiers(modifiersTree, request);
                    break;
            }
        }
        super.collect(request);
    }
}
