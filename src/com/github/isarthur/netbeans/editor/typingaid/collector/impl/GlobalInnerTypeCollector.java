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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.collector.api.InnerTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.filter.api.Filter;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class GlobalInnerTypeCollector extends InnerTypeCollector {

    public GlobalInnerTypeCollector(Filter... filters) {
        super(filters);
    }

    @Override
    protected Map<TypeElement, List<TypeElement>> collectInnerTypesByTopLevelTypes(CodeCompletionRequest request) {
        return JavaSourceUtilities.collectGlobalInnerTypeElements(request);
    }
}
