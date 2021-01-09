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

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.AbstractCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class CatchCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        switch (codeFragment.getKind()) {
            case EXTERNAL_TYPE:
            case GLOBAL_TYPE:
            case INTERNAL_TYPE:
                WorkingCopy copy = request.getWorkingCopy();
                TreeMaker make = copy.getTreeMaker();
                CatchTree originalTree = (CatchTree) request.getCurrentTree();
                VariableTree parameter = originalTree.getParameter();
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(originalTree);
                tokenSequence.moveStart();
                boolean afterLeftParenthesis = false;
                String identifier = null;
                while (tokenSequence.moveNext()) {
                    if (tokenSequence.token().id() == JavaTokenId.LPAREN) {
                        afterLeftParenthesis = true;
                    } else if (tokenSequence.token().id() == JavaTokenId.IDENTIFIER) {
                        if (afterLeftParenthesis) {
                            identifier = tokenSequence.token().text().toString();
                            break;
                        }
                    } else if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                        break;
                    }
                }
                VariableTree newParameter =
                        make.Variable(
                                parameter.getModifiers(),
                                identifier != null ? identifier : "e", //NOI18N
                                make.Type(codeFragment.toString()),
                                parameter.getInitializer());
                return make.Catch(newParameter, originalTree.getBlock());
            default:
                return null;
        }
    }
}
