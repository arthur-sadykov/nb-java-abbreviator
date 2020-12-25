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
package com.github.isarthur.netbeans.editor.typingaid.request.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.abbreviation.impl.JavaAbbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.context.api.CodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.context.impl.CodeCompletionContextFactory;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class CodeCompletionRequestImpl implements CodeCompletionRequest {

    private final Abbreviation abbreviation;
    private final List<CodeFragment> codeFragments;
    private WorkingCopy workingCopy;
    private CodeCompletionContext context;
    private TreePath currentPath;
    private Tree currentTree;
    private Tree.Kind currentKind;
    private final JTextComponent component;

    public CodeCompletionRequestImpl(Abbreviation abbreviation, List<CodeFragment> codeFragments,
            WorkingCopy workingCopy,
            JTextComponent component) {
        this.abbreviation = new JavaAbbreviation(abbreviation.getContent(), abbreviation.getStartOffset());
        this.codeFragments = codeFragments;
        this.workingCopy = workingCopy;
        TreeUtilities treeUtilities = workingCopy.getTreeUtilities();
        currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        currentTree = currentPath.getLeaf();
        currentKind = currentTree.getKind();
        context = CodeCompletionContextFactory.getCodeCompletionContext(currentKind);
        this.component = component;
    }

    @Override
    public Abbreviation getAbbreviation() {
        return abbreviation;
    }

    @Override
    public List<CodeFragment> getCodeFragments() {
        return codeFragments;
    }

    @Override
    public WorkingCopy getWorkingCopy() {
        return workingCopy;
    }

    @Override
    public TreePath getCurrentPath() {
        return currentPath;
    }

    @Override
    public CodeCompletionContext getContext() {
        return context;
    }

    @Override
    public Tree getCurrentTree() {
        return currentTree;
    }

    @Override
    public Tree.Kind getCurrentKind() {
        return currentKind;
    }

    @Override
    public JTextComponent getComponent() {
        return component;
    }

    @Override
    public void update(WorkingCopy workingCopy) {
        this.workingCopy = workingCopy;
        TreeUtilities treeUtilities = workingCopy.getTreeUtilities();
        currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        currentTree = currentPath.getLeaf();
        currentKind = currentTree.getKind();
        context = CodeCompletionContextFactory.getCodeCompletionContext(currentKind);
    }
}
