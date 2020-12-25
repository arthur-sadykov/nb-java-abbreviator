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
package com.github.isarthur.netbeans.editor.typingaid.context.impl;

import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CodeFragmentCollectorLinkerImpl;
import com.github.isarthur.netbeans.editor.typingaid.context.api.AbstractCodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.UnaryExpressionCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import static com.sun.source.tree.Tree.Kind.PREFIX_DECREMENT;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 *
 * @author Arthur Sadykov
 */
public class PrefixDecrementCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        if (!request.getAbbreviation().isSimple()) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkMethodInvocationCollector()
                    .linkStaticFieldAccessCollector()
                    .linkStaticMethodInvocationCollector()
                    .build();
        }
        return CodeFragmentCollectorLinkerImpl.builder()
                .linkEnumConstantCollector()
                .linkExceptionParameterCollector()
                .linkFieldCollector()
                .linkLiteralCollector()
                .linkLocalMethodInvocationCollector()
                .linkLocalVariableCollector()
                .linkParameterCollector()
                .linkResourceVariableCollector()
                .build();
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new UnaryExpressionCodeFragmentInsertVisitor(PREFIX_DECREMENT);
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        Types types = request.getWorkingCopy().getTypes();
        return types.getPrimitiveType(TypeKind.LONG);
    }
}
