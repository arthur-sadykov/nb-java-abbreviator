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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.impl.InternalType;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class InternalTypeCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        List<Element> localElements = new ArrayList<>();
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        ElementUtilities elementUtilities = copy.getElementUtilities();
        Elements elements = copy.getElements();
        Abbreviation abbreviation = request.getAbbreviation();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        CompilationUnitTree compilationUnit = copy.getCompilationUnit();
        List<? extends Tree> typeDecls = compilationUnit.getTypeDecls();
        Tree topLevelClassInterfaceOrEnumTree = typeDecls.get(0);
        Element topLevelElement = copy.getTrees().getElement(
                TreePath.getPath(compilationUnit, topLevelClassInterfaceOrEnumTree));
        localElements.add(topLevelElement);
        Iterable<? extends Element> localMembersAndVars =
                elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                    return (!elements.isDeprecated(e)
                            && (e.getKind() == ElementKind.CLASS
                            || e.getKind() == ElementKind.ENUM
                            || e.getKind() == ElementKind.INTERFACE));
                });
        localMembersAndVars.forEach(localElements::add);
        List<CodeFragment> codeFragments = request.getCodeFragments();
        localElements
                .stream()
                .filter(element -> StringUtilities.getElementAbbreviation(
                        element.getSimpleName().toString()).equals(abbreviation.getContent()))
                .filter(distinctByKey(Element::getSimpleName))
                .forEach(element -> codeFragments.add(new InternalType(
                        ElementHandle.create(elements.getTypeElement(element.toString())),
                        ((TypeElement) element).getTypeParameters().size())));
        super.collect(request);
    }
}
