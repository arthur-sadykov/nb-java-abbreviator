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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.GlobalType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class GlobalThrowableTypeCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Iterable<? extends TypeElement> globalTypes =
                collectGlobalThrowableTypeElements(copy, request.getAbbreviation());
        List<CodeFragment> codeFragments = request.getCodeFragments();
        globalTypes.forEach(globalType -> codeFragments.add(new GlobalType(globalType)));
        super.collect(request);
    }

    private Iterable<? extends TypeElement> collectGlobalThrowableTypeElements(
            WorkingCopy copy, Abbreviation abbreviation) {
        Elements elements = copy.getElements();
        ElementUtilities elementUtilities = copy.getElementUtilities();
        TypeMirror throwableTypeMirror = elements.getTypeElement("java.lang.Throwable").asType(); //NOI18N
        Types types = copy.getTypes();
        return elementUtilities.getGlobalTypes((element, type) -> {
            if (!elements.isDeprecated(element)) {
                String typeAbbreviation = StringUtilities.getElementAbbreviation(element.getSimpleName().toString());
                if (typeAbbreviation.equals(abbreviation.getScope())) {
                    if (types.isAssignable(element.asType(), throwableTypeMirror)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }
}
