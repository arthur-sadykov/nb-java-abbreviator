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

import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.BREAK_KEYWORD;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.api.AbstractKeyword;
import com.github.isarthur.netbeans.editor.typingaid.collector.visitor.api.KeywordCollectVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.BREAK;
import static com.sun.source.tree.Tree.Kind.DO_WHILE_LOOP;
import static com.sun.source.tree.Tree.Kind.ENHANCED_FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.SWITCH;
import static com.sun.source.tree.Tree.Kind.WHILE_LOOP;
import java.util.EnumSet;

/**
 *
 * @author Arthur Sadykov
 */
public class BreakKeyword extends AbstractKeyword {

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
        return BREAK_KEYWORD;
    }

    @Override
    public Tree.Kind getTreeKind() {
        return BREAK;
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        if (!JavaSourceUtilities.getParentTreeOfKind(EnumSet.of(DO_WHILE_LOOP, ENHANCED_FOR_LOOP,
                FOR_LOOP, SWITCH, WHILE_LOOP), request)) {
            return null;
        }
        return JavaSourceMaker.makeBreakTree(request);
    }

    @Override
    public String toString() {
        return "break"; //NOI18N
    }
}
