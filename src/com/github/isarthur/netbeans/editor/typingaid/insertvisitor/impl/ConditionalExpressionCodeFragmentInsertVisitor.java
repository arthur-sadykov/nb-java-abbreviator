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
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class ConditionalExpressionCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TokenSequence<?> tokenSequence = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        tokenSequence.move(abbreviation.getStartOffset());
        while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        Token<?> token = tokenSequence.token();
        boolean afterQuestion = false;
        if (token != null) {
            if (token.id() == JavaTokenId.QUESTION) {
                afterQuestion = true;
            } else if (token.id() == JavaTokenId.COLON) {
                afterQuestion = false;
            } else {
                return null;
            }
        }
        ConditionalExpressionTree originalTree = (ConditionalExpressionTree) getOriginalTree(codeFragment, request);
        if (afterQuestion) {
            return JavaSourceMaker.makeConditionalExpressionTree(
                    originalTree.getCondition(), (ExpressionTree) tree, originalTree.getFalseExpression(), request);
        } else {
            return JavaSourceMaker.makeConditionalExpressionTree(
                    originalTree.getCondition(), originalTree.getTrueExpression(), (ExpressionTree) tree, request);
        }
    }
}
