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
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class MemberSelectCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        MemberSelectTree originalTree = (MemberSelectTree) request.getCurrentTree();
        MemberSelectTree memberSelectTree = JavaSourceMaker.makeMemberSelectTree(
                originalTree.getExpression(), tree.toString(), request);
        String identifier = memberSelectTree.toString();
        long dotCount = identifier.chars().filter(ch -> ch == '.').count();
        WorkingCopy copy = request.getWorkingCopy();
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokenSequence = copy.getTokenHierarchy().tokenSequence();
        tokenSequence.move(abbreviation.getStartOffset());
        while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        Token<?> token = tokenSequence.token();
        if (token != null) {
            if (dotCount > 1) {
                if (token.id() == JavaTokenId.COMMA) {
                    identifier += ","; //NOI18N
                } else if (token.id() == JavaTokenId.RBRACKET) {
                    identifier += "]"; //NOI18N
                } else if (token.id() == JavaTokenId.RPAREN) {
                    identifier += ")"; //NOI18N
                } else if (token.id() == JavaTokenId.SEMICOLON) {
                    identifier += ";"; //NOI18N
                }
            }
        }
        return JavaSourceMaker.makeIdentifierTree(identifier, request);
    }
}
