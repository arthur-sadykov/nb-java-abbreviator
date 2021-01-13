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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl.StaticFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class StaticFieldAccessForGlobalTypesCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        Abbreviation abbreviation = request.getAbbreviation();
        Iterable<? extends TypeElement> typeElements = collectGlobalTypeElements(request.getWorkingCopy(), abbreviation);
        typeElements.forEach(typeElement -> {
            try {
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                enclosedElements.stream().filter(element ->
                        ((element.getKind() == ElementKind.FIELD
                        && element.getModifiers().contains(Modifier.PUBLIC)
                        && element.getModifiers().contains(Modifier.STATIC)
                        && element.getModifiers().contains(Modifier.FINAL))
                        || element.getKind() == ElementKind.ENUM_CONSTANT)).forEachOrdered(element -> {
                    String elementName = element.getSimpleName().toString();
                    String elementAbbreviation = StringUtilities.getElementAbbreviation(elementName);
                    if (abbreviation.getIdentifier().equals(elementAbbreviation)) {
                        List<CodeFragment> codeFragments = request.getCodeFragments();
                        codeFragments.add(new StaticFieldAccess(ElementHandle.create(typeElement), element));
                    }
                });
            } catch (AssertionError ex) {
            }
        });
        super.collect(request);
    }

    private Iterable<? extends TypeElement> collectGlobalTypeElements(WorkingCopy copy, Abbreviation abbreviation) {
        ElementUtilities elementUtilities = copy.getElementUtilities();
        return elementUtilities.getGlobalTypes((element, type) -> {
            String typeAbbreviation = StringUtilities.getElementAbbreviation(element.getSimpleName().toString());
            return typeAbbreviation.equals(abbreviation.getScope());
        });
    }
}
