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
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;

/**
 *
 * @author Arthur Sadykov
 */
public class UnaryExpressionCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    private final Tree.Kind kind;

    public UnaryExpressionCodeFragmentInsertVisitor(Tree.Kind kind) {
        this.kind = kind;
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        if (ExpressionStatementTree.class.isInstance(tree)) {
            return JavaSourceMaker.makeUnaryTree(
                    kind, JavaSourceMaker.makeIdentifierTree(tree.toString(), request), request);
        }
        return JavaSourceMaker.makeUnaryTree(kind, (ExpressionTree) tree, request);
    }
}
