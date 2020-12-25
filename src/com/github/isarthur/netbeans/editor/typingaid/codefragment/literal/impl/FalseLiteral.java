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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl;

import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.FALSE_LITERAL;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.api.AbstractLiteral;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;

/**
 *
 * @author Arthur Sadykov
 */
public class FalseLiteral extends AbstractLiteral {

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Object getIdentifier() {
        return false;
    }

    @Override
    public Kind getKind() {
        return FALSE_LITERAL;
    }

    @Override
    public String toString() {
        return "false"; //NOI18N
    }
}
