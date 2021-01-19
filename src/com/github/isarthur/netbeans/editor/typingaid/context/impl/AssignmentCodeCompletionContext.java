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

import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CodeFragmentCollectorLinkerImpl;
import com.github.isarthur.netbeans.editor.typingaid.context.api.AbstractCodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.AssignmentCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.util.TreePath;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Arthur Sadykov
 */
public class AssignmentCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        if (!request.getAbbreviation().isSimple()) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkExternalInnerTypeCollector()
                    .linkGlobalInnerTypeCollector()
                    .linkMethodInvocationCollector()
                    .linkStaticFieldAccessCollector()
                    .linkStaticMethodInvocationCollector()
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
                .linkLiteralCollector()
                .linkLocalMethodInvocationCollector()
                .linkLocalVariableCollector()
                .linkParameterCollector()
                .linkPrimitiveTypeCollector()
                .linkResourceVariableCollector()
                .build();
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new AssignmentCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        TreePath currentPath = request.getCurrentPath();
        AssignmentTree assignmentTree = (AssignmentTree) currentPath.getLeaf();
        ExpressionTree variable = assignmentTree.getVariable();
        TreePath path = TreePath.getPath(currentPath, variable);
        return request.getWorkingCopy().getTrees().getElement(path).asType();
    }
}
