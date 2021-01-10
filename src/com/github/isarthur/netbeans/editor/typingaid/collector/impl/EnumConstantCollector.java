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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.localelement.impl.LocalElementImpl;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.LocalElementCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import java.util.List;
import javax.lang.model.element.Element;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class EnumConstantCollector extends LocalElementCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        TypeMirror typeMirror = JavaSourceUtilities.getTypeInContext(request);
        if (typeMirror != null) {
            WorkingCopy copy = request.getWorkingCopy();
            ElementUtilities elementUtilities = copy.getElementUtilities();
            Iterable<? extends Element> enumConstants = elementUtilities.getMembers(typeMirror, (element, type) -> {
                return element.getKind() == ENUM_CONSTANT;
            });
            Abbreviation abbreviation = request.getAbbreviation();
            List<CodeFragment> codeFragments = request.getCodeFragments();
            enumConstants.forEach(enumConstant -> {
                String enumConstantAbbreviation =
                        StringUtilities.getElementAbbreviation(enumConstant.getSimpleName().toString());
                if (enumConstantAbbreviation.equals(abbreviation.getContent())) {
                    codeFragments.add(new LocalElementImpl(enumConstant));
                }
            });
        }
        super.collect(request);
    }
}
