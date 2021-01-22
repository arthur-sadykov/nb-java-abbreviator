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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.GlobalType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.filter.api.Filter;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author Arthur Sadykov
 */
public class GlobalTypeCollector extends AbstractCodeFragmentCollector {

    private final Filter[] filters;

    public GlobalTypeCollector(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void collect(CodeCompletionRequest request) {
        List<TypeElement> globalTypes = JavaSourceUtilities.collectGlobalTypeElements(request);
        for (Filter filter : filters) {
            globalTypes = filter.meetCriteria(globalTypes);
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        codeFragments.addAll(
                globalTypes.stream()
                        .map(globalType ->
                                new GlobalType(ElementHandle.create(globalType), globalType.getTypeParameters().size()))
                        .collect(Collectors.toList()));
        super.collect(request);
    }
}
