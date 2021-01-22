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
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.CatchCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.Tree;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class CatchCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Tree tree = request.getCurrentTree();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(tree);
        tokenSequence.move(abbreviation.getStartOffset());
        tokenSequence.moveStart();
        boolean beforeLeftParenthesis = false;
        boolean afterRightParenthesis = false;
        while (tokenSequence.moveNext()) {
            if (tokenSequence.token().id() == JavaTokenId.LPAREN) {
                if (abbreviation.getStartOffset() <= tokenSequence.offset()) {
                    beforeLeftParenthesis = true;
                    break;
                }
            } else if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                    afterRightParenthesis = true;
                    break;
                }
            }
        }
        if (!beforeLeftParenthesis && !afterRightParenthesis) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkExternalThrowableTypeCollector(request)
                    .linkGlobalThrowableTypeCollector(request)
                    .linkInternalThrowableTypeCollector(request)
                    .build();
        }
        return CodeFragmentCollectorLinkerImpl.builder()
                .build();
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new CatchCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        return null;
    }
}
