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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.ExternalType;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class ExternalThrowableTypeCollector extends ExternalTypeCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        List<TypeElement> types = new ArrayList<>();
        List<TypeElement> externalTypeElements = collectExternalTypeElements(request);
        types.addAll(filterThrowableTypes(externalTypeElements, request));
        List<CodeFragment> codeFragments = request.getCodeFragments();
        codeFragments.addAll(
                types.stream()
                        .filter(distinctByKey(element -> element.getSimpleName().toString()))
                        .map(ExternalType::new)
                        .sorted()
                        .collect(Collectors.toList()));
        super.collect(request);
    }
}
