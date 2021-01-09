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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.api.PrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BooleanPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BytePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.CharPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.DoublePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.FloatPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.IntPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.LongPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.ShortPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class PrimitiveTypeCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        Abbreviation abbreviation = request.getAbbreviation();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return;
        }
        Tree currentTree = currentPath.getLeaf();
        List<PrimitiveType> primitiveTypes = Arrays.asList(
                new BooleanPrimitiveType(),
                new BytePrimitiveType(),
                new CharPrimitiveType(),
                new DoublePrimitiveType(),
                new FloatPrimitiveType(),
                new IntPrimitiveType(),
                new LongPrimitiveType(),
                new ShortPrimitiveType()
        );
        Supplier<Void> collectPrimitiveTypes = () -> {
            primitiveTypes.forEach(primitiveType -> {
                if (primitiveType.isAbbreviationEqualTo(abbreviation.toString())) {
                    List<CodeFragment> codeFragments = request.getCodeFragments();
                    codeFragments.add(primitiveType);
                }
            });
            return null;
        };
        switch (currentTree.getKind()) {
            case CLASS:
            case ENUM:
                Supplier<Boolean> abbreviationInsideBraces = () -> {
                    TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(currentTree);
                    tokenSequence.moveStart();
                    TokenId tokenId = null;
                    while (tokenSequence.moveNext()) {
                        tokenId = tokenSequence.token().id();
                        if (tokenId == JavaTokenId.LBRACE) {
                            break;
                        }
                    }
                    if (tokenId == JavaTokenId.LBRACE) {
                        if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                            return true;
                        }
                    }
                    return false;
                };
                if (abbreviationInsideBraces.get()) {
                    collectPrimitiveTypes.get();
                }
                break;
            case BLOCK:
                collectPrimitiveTypes.get();
                break;
            case METHOD:
                Supplier<Boolean> abbreviationInsideParetheses = () -> {
                    TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(currentTree);
                    tokenSequence.moveStart();
                    TokenId tokenId = null;
                    while (tokenSequence.moveNext()) {
                        tokenId = tokenSequence.token().id();
                        if (tokenId == JavaTokenId.LPAREN) {
                            int leftParenthesisPosition = tokenSequence.offset();
                            if (abbreviation.getStartOffset() <= leftParenthesisPosition) {
                                return false;
                            }
                        } else if (tokenId == JavaTokenId.LBRACE || tokenId == JavaTokenId.SEMICOLON) {
                            while (tokenSequence.movePrevious()) {
                                if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                                    return abbreviation.getStartOffset() <= tokenSequence.offset();
                                }
                            }
                        }
                    }
                    return false;
                };
                if (abbreviationInsideParetheses.get()) {
                    collectPrimitiveTypes.get();
                }
                break;
            case VARIABLE:
                Supplier<Boolean> abbreviationAfterEQToken = () -> {
                    TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(currentTree);
                    tokenSequence.moveStart();
                    TokenId tokenId = null;
                    while (tokenSequence.moveNext()) {
                        tokenId = tokenSequence.token().id();
                        if (tokenId == JavaTokenId.EQ) {
                            break;
                        }
                    }
                    if (tokenId == JavaTokenId.EQ) {
                        if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                            return true;
                        }
                    }
                    return false;
                };
                if (abbreviationAfterEQToken.get()) {
                    collectPrimitiveTypes.get();
                }
                break;
        }
        super.collect(request);
    }
}
