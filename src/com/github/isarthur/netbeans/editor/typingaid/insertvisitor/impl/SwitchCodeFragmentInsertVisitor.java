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
package com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.AbstractCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.CASE;
import java.util.List;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class SwitchCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        SwitchTree switchTree = (SwitchTree) request.getCurrentTree();
        List<? extends CaseTree> cases = switchTree.getCases();
        if (cases.isEmpty()) {
            return switchTree;
        }
        Abbreviation abbreviation = request.getAbbreviation();
        WorkingCopy copy = request.getWorkingCopy();
        int insertIndex = JavaSourceUtilities.findInsertIndexForTree(abbreviation.getStartOffset(), cases, copy);
        if (insertIndex == -1) {
            return null;
        }
        return insertIndex == 0 ? switchTree : cases.get(insertIndex - 1);
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        Tree originalTree = getOriginalTree(codeFragment, request);
        WorkingCopy copy = request.getWorkingCopy();
        TreeMaker make = copy.getTreeMaker();
        Abbreviation abbreviation = request.getAbbreviation();
        if (originalTree.getKind() == CASE) {
            CaseTree caseTree = (CaseTree) originalTree;
            int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                    abbreviation.getStartOffset(), caseTree.getStatements(), request.getWorkingCopy());
            if (insertIndex == -1) {
                return null;
            }
            return make.insertCaseStatement(caseTree, insertIndex, (StatementTree) tree);
        } else {
            SwitchTree switchTree = (SwitchTree) originalTree;
            int insertIndex = JavaSourceUtilities.findInsertIndexForTree(
                    abbreviation.getStartOffset(), switchTree.getCases(), request.getWorkingCopy());
            if (insertIndex == -1) {
                return null;
            }
            return make.insertSwitchCase(switchTree, 0, (CaseTree) tree);
        }
    }
}
