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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl.ChainedFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.ChainedMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.util.SourcePositions;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class MemberSelectCodeFragmentInsertVisitor implements CodeFragmentInsertVisitor {

    @Override
    public void visit(ChainedMethodInvocation methodInvocation, CodeCompletionRequest request) {
        MemberSelectTree memberSelectTree = (MemberSelectTree) request.getCurrentTree();
        WorkingCopy copy = request.getWorkingCopy();
        ExpressionTree methodInvocationTree =
                JavaSourceMaker.makeMethodInvocationExpressionTree(methodInvocation, request);
        MemberSelectTree newMemberSelectTree = JavaSourceMaker.makeMemberSelectTree(
                memberSelectTree.getExpression(), methodInvocationTree.toString(), request);
        SourcePositions sourcePositions = copy.getTrees().getSourcePositions();
        long startPosition = sourcePositions.getStartPosition(copy.getCompilationUnit(), memberSelectTree);
        long endPosition = sourcePositions.getEndPosition(copy.getCompilationUnit(), memberSelectTree);
        long dotCount = memberSelectTree.toString().chars().filter(ch -> ch == '.').count();
        String next = ""; //NOI18N
        if (dotCount > 0) {
            TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
            Abbreviation abbreviation = request.getAbbreviation();
            sequence.move(abbreviation.getStartOffset());
            while (sequence.moveNext() && sequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            next = Character.toString(sequence.token().text().charAt(0));
        }
        try {
            JTextComponent component = request.getComponent();
            Document document = component.getDocument();
            document.remove((int) startPosition, (int) (endPosition - startPosition));
            if (next.isEmpty()) {
                document.insertString((int) startPosition, newMemberSelectTree.toString(), null);
            } else {
                document.insertString((int) startPosition, newMemberSelectTree.toString() + next, null);
                component.setCaretPosition(component.getCaretPosition() - 1);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void visit(ChainedFieldAccess fieldAccess, CodeCompletionRequest request) {
        MemberSelectTree memberSelectTree = (MemberSelectTree) request.getCurrentTree();
        WorkingCopy copy = request.getWorkingCopy();
        MemberSelectTree newMemberSelectTree =
                JavaSourceMaker.makeMemberSelectTree(memberSelectTree.getExpression(), fieldAccess.toString(), request);
        SourcePositions sourcePositions = copy.getTrees().getSourcePositions();
        long startPosition = sourcePositions.getStartPosition(copy.getCompilationUnit(), memberSelectTree);
        long endPosition = sourcePositions.getEndPosition(copy.getCompilationUnit(), memberSelectTree);
        try {
            JTextComponent component = request.getComponent();
            Document document = component.getDocument();
            document.remove((int) startPosition, (int) (endPosition - startPosition));
            document.insertString((int) startPosition, newMemberSelectTree.toString(), null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
