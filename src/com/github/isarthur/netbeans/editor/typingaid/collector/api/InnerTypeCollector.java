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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.impl.InnerTypeImpl;
import com.github.isarthur.netbeans.editor.typingaid.collector.filter.api.Filter;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class InnerTypeCollector extends AbstractCodeFragmentCollector {

    private final Filter[] filters;

    protected InnerTypeCollector(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void collect(CodeCompletionRequest request) {
        Map<TypeElement, List<TypeElement>> innerTypesByGlobalTypes = collectInnerTypesByTopLevelTypes(request);
        List<CodeFragment> codeFragments = request.getCodeFragments();
        innerTypesByGlobalTypes.entrySet().forEach(entry -> {
            TypeElement externalType = entry.getKey();
            List<TypeElement> innerTypes = entry.getValue();
            for (Filter filter : filters) {
                innerTypes = filter.meetCriteria(innerTypes);
            }
            innerTypes.forEach(innerType -> {
                codeFragments.add(new InnerTypeImpl(
                        ElementHandle.create(externalType),
                        ElementHandle.create(innerType),
                        innerType.getTypeParameters().size()));
            });
        });
        super.collect(request);
    }

    protected abstract Map<TypeElement, List<TypeElement>> collectInnerTypesByTopLevelTypes(
            CodeCompletionRequest request);
}
