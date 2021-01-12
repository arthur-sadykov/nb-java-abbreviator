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

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.impl.GlobalInnerType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class GlobalInnerTypeCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Iterable<? extends TypeElement> globalTypes = collectGlobalTypeElements(request);
        Elements elements = copy.getElements();
        ElementUtilities elementUtilities = copy.getElementUtilities();
        List<CodeFragment> codeFragments = request.getCodeFragments();
        Abbreviation abbreviation = request.getAbbreviation();
        for (TypeElement globalType : globalTypes) {
            Iterable<? extends Element> innerTypeElements =
                    elementUtilities.getMembers(globalType.asType(), (element, type) -> {
                        if (elements.isDeprecated(element)) {
                            return false;
                        }
                        if (element.getKind() != ElementKind.ENUM && element.getKind() != ElementKind.CLASS) {
                            return false;
                        }
                        String innerTypeAbbreviation =
                                StringUtilities.getElementAbbreviation(element.getSimpleName().toString());
                        if (!innerTypeAbbreviation.equals(abbreviation.getIdentifier())) {
                            return false;
                        }
                        if (element.getModifiers().contains(Modifier.PUBLIC)) {
                            return true;
                        } else if (element.getModifiers().contains(Modifier.PRIVATE)) {
                            return false;
                        } else {
                            if (JavaSourceUtilities.inSamePackageAsCurrentFile(globalType, request)) {
                                return true;
                            }
                        }
                        return false;
                    });
            innerTypeElements.forEach(innerType ->
                    codeFragments.add(new GlobalInnerType(globalType, (TypeElement) innerType)));
        }
        super.collect(request);
    }

    protected Iterable<? extends TypeElement> collectGlobalTypeElements(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Elements elements = copy.getElements();
        Abbreviation abbreviation = request.getAbbreviation();
        ElementUtilities elementUtilities = copy.getElementUtilities();
        return elementUtilities.getGlobalTypes((element, type) -> {
            if (elements.isDeprecated(element)) {
                return false;
            }
            String typeAbbreviation = StringUtilities.getElementAbbreviation(element.getSimpleName().toString());
            if (!typeAbbreviation.equals(abbreviation.getScope())) {
                return false;
            }
            if (element.getModifiers().contains(Modifier.PUBLIC)) {
                return true;
            } else if (element.getModifiers().contains(Modifier.PRIVATE)) {
                return false;
            } else {
                if (JavaSourceUtilities.inSamePackageAsCurrentFile(element, request)) {
                    return true;
                }
            }
            return false;
        });
    }
}
