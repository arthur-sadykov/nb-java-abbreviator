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
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodModifierCollector extends ModifierCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        tokens.move(abbreviation.getStartOffset());
        while (tokens.movePrevious() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        TokenId tokenId = tokens.token().id();
        if (JavaSourceUtilities.isModifier(tokenId)) {
            MethodTree method = (MethodTree) request.getCurrentPath().getLeaf();
            ModifiersTree modifiersTree = method.getModifiers();
            collectMethodModifiers(modifiersTree, request);
        } else if (tokenId == JavaTokenId.COMMA || tokenId == JavaTokenId.LPAREN) {
            while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
            }
            TreePath parameterPath = treeUtilities.pathFor(tokens.offset());
            if (parameterPath != null) {
                Tree.Kind kind = parameterPath.getLeaf().getKind();
                if (kind == Tree.Kind.VARIABLE) {
                    VariableTree variable = (VariableTree) parameterPath.getLeaf();
                    ModifiersTree modifiersTree = variable.getModifiers();
                    collectVariableModifiers(modifiersTree, request);
                }
            }
        }
        super.collect(request);
    }
}
