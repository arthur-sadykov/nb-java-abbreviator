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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl;

import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.FOR_KEYWORD;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.api.AbstractKeyword;
import com.github.isarthur.netbeans.editor.typingaid.collector.visitor.api.KeywordCollectVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.FOR_LOOP;

/**
 *
 * @author Arthur Sadykov
 */
public class ForKeyword extends AbstractKeyword {

    @Override
    public void accept(KeywordCollectVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Kind getKind() {
        return FOR_KEYWORD;
    }

    @Override
    public Tree.Kind getTreeKind() {
        return FOR_LOOP;
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        return JavaSourceMaker.makeForLoopTree(request);
    }

    @Override
    public String toString() {
        return "for"; //NOI18N
    }
}
