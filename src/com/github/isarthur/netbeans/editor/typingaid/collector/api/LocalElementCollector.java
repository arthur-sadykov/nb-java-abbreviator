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
package com.github.isarthur.netbeans.editor.typingaid.collector.api;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.localelement.impl.LocalElementImpl;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.Scope;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class LocalElementCollector extends AbstractCodeFragmentCollector {

    protected void collect(CodeCompletionRequest request, ElementKind kind) {
        WorkingCopy copy = request.getWorkingCopy();
        List<Element> localElements = new ArrayList<>();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        Abbreviation abbreviation = request.getAbbreviation();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return;
        }
        if (TreeUtilities.CLASS_TREE_KINDS.contains(currentPath.getLeaf().getKind())) {
            return;
        }
        ElementUtilities elementUtilities = copy.getElementUtilities();
        Elements elements = copy.getElements();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        Iterable<? extends Element> localMembersAndVars =
                elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                    return (!elements.isDeprecated(e))
                            && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                            && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                            && e.getKind() == kind;
                });
        localMembersAndVars.forEach(localElements::add);
        List<CodeFragment> codeFragments = request.getCodeFragments();
        localElements
                .stream()
                .filter(element -> StringUtilities.getElementAbbreviation(
                        element.getSimpleName().toString()).equals(abbreviation.getIdentifier()))
                .filter(distinctByKey(Element::getSimpleName))
                .forEach(element -> codeFragments.add(new LocalElementImpl(element)));
    }
}
