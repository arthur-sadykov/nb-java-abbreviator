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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl.ChainedFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.context.api.CodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.context.impl.CodeCompletionContextFactory;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class ChainedEnumConstantAccessCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        TreePath currentPath = request.getCurrentPath();
        CodeCompletionContext context =
                CodeCompletionContextFactory.getCodeCompletionContext(currentPath.getLeaf().getKind());
        TypeMirror type = context.getType(request);
        if (type == null) {
            return;
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return;
        }
        List<VariableElement> enumConstants = getEnumConstants(typeElement, copy);
        enumConstants = getEnumConstantsByAbbreviation(enumConstants, request.getAbbreviation());
        List<CodeFragment> codeFragments = request.getCodeFragments();
        enumConstants.forEach(enumConstant -> codeFragments.add(new ChainedFieldAccess(enumConstant)));
        super.collect(request);
    }

    private List<VariableElement> getEnumConstants(Element element, WorkingCopy copy) {
        List<VariableElement> enumConstants = new ArrayList<>();
        ElementUtilities elementUtilities = copy.getElementUtilities();
        Elements elements = copy.getElements();
        TypeMirror typeMirror = element.asType();
        Iterable<? extends Element> members;
        try {
            members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                return !elements.isDeprecated(e) && e.getKind() == ElementKind.ENUM_CONSTANT;
            });
        } catch (AssertionError error) {
            return Collections.emptyList();
        }
        members.forEach(member -> enumConstants.add((VariableElement) member));
        return Collections.unmodifiableList(enumConstants);
    }

    private List<VariableElement> getEnumConstantsByAbbreviation(
            List<VariableElement> enumConstants, Abbreviation abbreviation) {
        List<VariableElement> result = new ArrayList<>();
        enumConstants.forEach(enumConstant -> {
            String enumConstantAbbreviation =
                    StringUtilities.getElementAbbreviation(enumConstant.getSimpleName().toString());
            if (enumConstantAbbreviation.equals(abbreviation.getIdentifier())) {
                result.add(enumConstant);
            }
        });
        return Collections.unmodifiableList(result);
    }
}
