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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl;

import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.PROTECTED_MODIFIER;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.api.AbstractModifier;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.PROTECTED;

/**
 *
 * @author Arthur Sadykov
 */
public class ProtectedModifier extends AbstractModifier {

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Modifier getIdentifier() {
        return PROTECTED;
    }

    @Override
    public Kind getKind() {
        return PROTECTED_MODIFIER;
    }
}
