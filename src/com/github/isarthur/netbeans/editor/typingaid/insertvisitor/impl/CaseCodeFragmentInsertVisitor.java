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
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class CaseCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        CaseTree originalTree = (CaseTree) request.getCurrentTree();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(originalTree);
        tokenSequence.moveStart();
        Abbreviation abbreviation = request.getAbbreviation();
        boolean afterColon = false;
        while (tokenSequence.moveNext()) {
            if (tokenSequence.token().id() == JavaTokenId.COLON) {
                if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                    afterColon = true;
                    break;
                }
            }
        }
        TreeMaker make = copy.getTreeMaker();
        if (afterColon) {
            int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                    abbreviation.getStartOffset(), originalTree.getStatements(), request.getWorkingCopy());
            if (insertIndex == -1) {
                return null;
            }
            return make.insertCaseStatement(originalTree, insertIndex, (StatementTree) tree);
        } else {
            return make.Case(make.Identifier(codeFragment.toString()), originalTree.getStatements());
        }
    }
}
