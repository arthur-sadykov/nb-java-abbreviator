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
package com.github.isarthur.netbeans.editor.typingaid.context.api;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.CodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CodeFragmentCollectorLinkerImpl;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.selector.api.CodeFragmentSelector;
import com.github.isarthur.netbeans.editor.typingaid.selector.impl.CodeFragmentSelectorFactory;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.ModificationResult;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractCodeCompletionContext implements CodeCompletionContext {

    @Override
    public void collect(CodeCompletionRequest request) {
        CodeFragmentCollectorLinkerImpl linker = getCodeFragmentCollectorLinker(request);
        CodeFragmentCollector collector = linker.link();
        collector.collect(request);
    }

    @Override
    public void insert(CodeFragment codeFragment, CodeCompletionRequest request) {
        CodeFragmentInsertVisitor visitor = getCodeFragmentInsertVisitor();
        codeFragment.accept(visitor, request);
    }

    @Override
    public void select(CodeFragment codeFragment, ModificationResult modificationResult, JTextComponent component) {
        CodeFragmentSelector selector =
                CodeFragmentSelectorFactory.getCodeFragmentSelector(codeFragment);
        selector.select(modificationResult, component);
    }

    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        if (!request.getAbbreviation().isSimple()) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkExternalInnerTypeCollector()
                    .linkGlobalInnerTypeCollector()
                    .linkMethodInvocationCollector()
                    .linkStaticFieldAccessCollector()
                    .linkStaticMethodInvocationCollector()
                    .build();
        }
        return CodeFragmentCollectorLinkerImpl.builder()
                .linkExceptionParameterCollector()
                .linkExternalTypeCollector()
                .linkFieldCollector()
                .linkGlobalTypeCollector()
                .linkInternalTypeCollector()
                .linkLiteralCollector()
                .linkLocalMethodInvocationCollector()
                .linkLocalVariableCollector()
                .linkParameterCollector()
                .linkResourceVariableCollector()
                .build();
    }

    protected abstract CodeFragmentInsertVisitor getCodeFragmentInsertVisitor();
}
