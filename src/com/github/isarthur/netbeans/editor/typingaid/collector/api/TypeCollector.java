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
package com.github.isarthur.netbeans.editor.typingaid.collector.api;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.TypeImpl;
import com.github.isarthur.netbeans.editor.typingaid.collector.filter.api.Filter;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class TypeCollector extends AbstractCodeFragmentCollector {

    private final Filter[] filters;

    protected TypeCollector(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void collect(CodeCompletionRequest request) {
        List<TypeElement> types = collectTypes(request);
        for (Filter filter : filters) {
            types = filter.meetCriteria(types);
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        codeFragments.addAll(
                types.stream()
                        .map(type -> new TypeImpl(ElementHandle.create(type), type.getTypeParameters().size()))
                        .collect(Collectors.toList()));
        super.collect(request);
    }

    protected abstract List<TypeElement> collectTypes(CodeCompletionRequest request);
}