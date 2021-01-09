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
package com.github.isarthur.netbeans.editor.typingaid.context.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CodeFragmentCollectorLinkerImpl;
import com.github.isarthur.netbeans.editor.typingaid.context.api.AbstractCodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.MethodCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import static com.sun.source.tree.Tree.Kind.METHOD;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(request.getCurrentTree());
        tokenSequence.moveStart();
        Abbreviation abbreviation = request.getAbbreviation();
        boolean beforeLeftParenthesis = false;
        boolean beforeRightParenthesis = false;
        boolean afterLeftBrace = false;
        OUTER:
        while (tokenSequence.moveNext()) {
            switch (tokenSequence.token().id()) {
                case LPAREN:
                    if (abbreviation.getStartOffset() < tokenSequence.offset()) {
                        beforeLeftParenthesis = true;
                        break OUTER;
                    }
                    break;
                case LBRACE:
                    if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                        afterLeftBrace = true;
                        break OUTER;
                    } else {
                        while (tokenSequence.movePrevious()) {
                            if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                                if (abbreviation.getStartOffset() <= tokenSequence.offset()) {
                                    beforeRightParenthesis = true;
                                }
                                break OUTER;
                            }
                        }
                    }
                    break;
                case SEMICOLON:
                    while (tokenSequence.movePrevious()) {
                        if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                            if (abbreviation.getStartOffset() <= tokenSequence.offset()) {
                                beforeRightParenthesis = true;
                            }
                            break OUTER;
                        }
                    }
                    break;
            }
        }
        if (beforeLeftParenthesis) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkModifierCollector(METHOD)
                    .build();
        } else if (beforeRightParenthesis) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkExternalTypeCollector()
                    .linkGlobalTypeCollector()
                    .linkInternalTypeCollector()
                    .linkPrimitiveTypeCollector()
                    .build();
        } else if (afterLeftBrace) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkExternalTypeCollector()
                    .linkGlobalTypeCollector()
                    .linkInternalTypeCollector()
                    .linkModifierCollector(METHOD)
                    .linkPrimitiveTypeCollector()
                    .build();
        } else {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .build();
        }
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new MethodCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        return null;
    }
}
