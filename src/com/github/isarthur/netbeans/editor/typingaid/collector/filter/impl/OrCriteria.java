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
package com.github.isarthur.netbeans.editor.typingaid.collector.filter.impl;

import com.github.isarthur.netbeans.editor.typingaid.collector.filter.api.Filter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class OrCriteria implements Filter {

    private final Filter[] criterias;

    public OrCriteria(Filter... criterias) {
        this.criterias = criterias;
    }

    @Override
    public List<TypeElement> meetCriteria(List<TypeElement> typeElements) {
        List<TypeElement> filteredTypeElements = typeElements;
        for (Filter filter : criterias) {
            List<TypeElement> currentTypeElements = filter.meetCriteria(filteredTypeElements);
            filteredTypeElements.addAll(
                    currentTypeElements.stream().filter(currentTypeElement ->
                            filteredTypeElements.stream()
                                    .noneMatch(filteredTypeElement -> currentTypeElement.equals(filteredTypeElement)))
                            .collect(Collectors.toList()));
        }
        return Collections.unmodifiableList(filteredTypeElements);
    }
}
