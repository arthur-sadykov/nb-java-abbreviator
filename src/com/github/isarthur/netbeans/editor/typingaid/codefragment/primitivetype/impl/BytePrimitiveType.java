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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl;

import static com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment.Kind.BYTE_PRIMITIVE_TYPE;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.api.AbstractPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import javax.lang.model.type.TypeKind;
import static javax.lang.model.type.TypeKind.BYTE;

/**
 *
 * @author Arthur Sadykov
 */
public class BytePrimitiveType extends AbstractPrimitiveType {

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Object getDefaultValue() {
        return 0;
    }

    @Override
    public Kind getKind() {
        return BYTE_PRIMITIVE_TYPE;
    }

    @Override
    public TypeKind getTypeKind() {
        return BYTE;
    }

    @Override
    public String toString() {
        return "byte"; //NOI18N
    }
}
