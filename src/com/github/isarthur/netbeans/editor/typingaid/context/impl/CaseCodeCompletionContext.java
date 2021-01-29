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
package com.github.isarthur.netbeans.editor.typingaid.context.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CodeFragmentCollectorLinkerImpl;
import com.github.isarthur.netbeans.editor.typingaid.context.api.AbstractCodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.CaseCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class CaseCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        Abbreviation abbreviation = request.getAbbreviation();
        Tree originalTree = request.getCurrentTree();
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(originalTree);
        tokenSequence.moveStart();
        boolean afterColon = false;
        while (tokenSequence.moveNext()) {
            if (tokenSequence.token().id() == JavaTokenId.COLON) {
                if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                    afterColon = true;
                    break;
                }
            }
        }
        if (afterColon) {
            if (!request.getAbbreviation().isSimple()) {
                return CodeFragmentCollectorLinkerImpl.builder()
                        .linkExternalInnerTypeCollector()
                        .linkExternalStaticMethodInvocationCollector()
                        .linkGlobalInnerTypeCollector()
                        .linkGlobalStaticMethodInvocationCollector()
                        .linkMethodInvocationCollector()
                        .build();
            }
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkEnumConstantCollector()
                    .linkExceptionParameterCollector()
                    .linkExternalTypeCollector()
                    .linkFieldCollector()
                    .linkGlobalTypeCollector()
                    .linkInternalTypeCollector()
                    .linkKeywordCollector()
                    .linkLocalMethodInvocationCollector()
                    .linkLocalVariableCollector()
                    .linkParameterCollector()
                    .linkPrimitiveTypeCollector()
                    .linkResourceVariableCollector()
                    .build();
        } else {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkEnumConstantCollector()
                    .build();
        }
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new CaseCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        TreeUtilities treeUtilities = request.getWorkingCopy().getTreeUtilities();
        TreePath currentPath = request.getCurrentPath();
        TreePath switchPath = treeUtilities.getPathElementOfKind(Tree.Kind.SWITCH, currentPath);
        if (switchPath == null) {
            return null;
        }
        SwitchTree switchTree = (SwitchTree) switchPath.getLeaf();
        ExpressionTree expression = switchTree.getExpression();
        TreePath expressionPath = TreePath.getPath(switchPath, expression);
        Trees trees = request.getWorkingCopy().getTrees();
        return trees.getTypeMirror(expressionPath);
    }
}
