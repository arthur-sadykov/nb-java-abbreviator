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

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.ExternalType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class ExternalTypeCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        List<TypeElement> types = new ArrayList<>();
        types.addAll(collectExternalTypeElements(request.getWorkingCopy(), request.getAbbreviation()));
        List<CodeFragment> codeFragments = request.getCodeFragments();
        codeFragments.addAll(
                types.stream()
                        .filter(distinctByKey(element -> element.getSimpleName().toString()))
                        .map(ExternalType::new)
                        .sorted()
                        .collect(Collectors.toList()));
        super.collect(request);
    }

    private List<TypeElement> collectExternalTypeElements(WorkingCopy copy, Abbreviation abbreviation) {
        ClasspathInfo classpathInfo = copy.getClasspathInfo();
        ClassIndex classIndex = classpathInfo.getClassIndex();
        Set<ElementHandle<TypeElement>> declaredTypes = classIndex.getDeclaredTypes(
                abbreviation.getScope().toUpperCase(),
                ClassIndex.NameKind.CAMEL_CASE,
                EnumSet.of(ClassIndex.SearchScope.SOURCE, ClassIndex.SearchScope.DEPENDENCIES));
        List<TypeElement> typeElements = new ArrayList<>();
        Elements elements = copy.getElements();
        declaredTypes.forEach(type -> {
            TypeElement typeElement = type.resolve(copy);
            if (typeElement != null) {
                String typeName = typeElement.getSimpleName().toString();
                String typeAbbreviation = StringUtilities.getElementAbbreviation(typeName);
                if (typeAbbreviation.equals(abbreviation.getScope())) {
                    if (!elements.isDeprecated(typeElement)) {
                        typeElements.add(typeElement);
                    }
                }
            }
        });
        return Collections.unmodifiableList(typeElements);
    }
}
