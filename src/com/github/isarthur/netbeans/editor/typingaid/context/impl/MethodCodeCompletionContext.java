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
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.MethodCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import static com.sun.source.tree.Tree.Kind.METHOD;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(request.getCurrentTree());
        tokenSequence.moveStart();
        Abbreviation abbreviation = request.getAbbreviation();
        CodeFragmentCollectorLinkerImpl.CodeFragmentCollectorLinkerBuilder builder =
                CodeFragmentCollectorLinkerImpl.builder();
        if (JavaSourceUtilities.isAdjacentToModifiersTreeSpan(request)) {
            builder.linkModifierCollector(METHOD);
        } else if (JavaSourceUtilities.isInsideMethodParameterTreeSpan(request)) {
            if (!abbreviation.isSimple()) {
                builder.linkExternalInnerTypeCollector()
                        .linkGlobalInnerTypeCollector();
            } else {
                builder.linkExternalTypeCollector()
                        .linkGlobalTypeCollector()
                        .linkInternalTypeCollector()
                        .linkPrimitiveTypeCollector();
            }
        } else if (JavaSourceUtilities.isPositionOfThrowsKeyword(request)) {
            builder.linkKeywordCollector();
        } else if (JavaSourceUtilities.isInsideThrowsTreeSpan(request)) {
            if (!abbreviation.isSimple()) {
                builder.linkExternalInnerThrowableTypeCollector(request)
                        .linkGlobalInnerThrowableTypeCollector(request);
            } else {
                builder.linkExternalThrowableTypeCollector(request)
                        .linkGlobalThrowableTypeCollector(request)
                        .linkInternalThrowableTypeCollector(request);
            }
        } else if (JavaSourceUtilities.isInsideMethodBodySpan(request)) {
            if (!abbreviation.isSimple()) {
                builder.linkExternalInnerTypeCollector()
                        .linkGlobalInnerTypeCollector();
            } else {
                builder
                        .linkExternalTypeCollector()
                        .linkGlobalTypeCollector()
                        .linkInternalTypeCollector()
                        .linkModifierCollector(METHOD)
                        .linkPrimitiveTypeCollector();
            }
        } else {
            throw new RuntimeException("Cannot find location in the method declaration."); //NOI18N
        }
        return builder.build();
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new MethodCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        return null;
    }
}
