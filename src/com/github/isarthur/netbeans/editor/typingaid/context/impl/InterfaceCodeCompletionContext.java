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
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.InterfaceCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.ClassTree;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;

/**
 *
 * @author Arthur Sadykov
 */
public class InterfaceCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        CodeFragmentCollectorLinkerImpl.CodeFragmentCollectorLinkerBuilder builder =
                CodeFragmentCollectorLinkerImpl.builder();
        Abbreviation abbreviation = request.getAbbreviation();
        if (JavaSourceUtilities.isAdjacentToModifiersTreeSpan(request)) {
            builder.linkModifierCollector(INTERFACE);
        } else if (JavaSourceUtilities.isPositionOfExtendsKeywordInClassOrInterfaceDeclaration(request)) {
            builder.linkKeywordCollector();
        } else if (JavaSourceUtilities.isInsideExtendsTreeSpan(request)) {
            if (!abbreviation.isSimple()) {
                builder.linkExternalInnerInterfaceCollector()
                        .linkGlobalInnerInterfaceCollector();
            } else {
                builder.linkExternalInterfaceCollector()
                        .linkGlobalInterfaceCollector()
                        .linkInternalInterfaceCollector();
            }
        } else if (JavaSourceUtilities.isInsideClassEnumOrInterfaceBodySpan((ClassTree) request.getCurrentTree(), request)) {
            if (JavaSourceUtilities.isNextToken(JavaTokenId.WHITESPACE, request)) {
                if (!abbreviation.isSimple()) {
                    builder.linkExternalInnerTypeCollector()
                            .linkGlobalInnerTypeCollector();
                } else {
                    builder.linkExternalTypeCollector()
                            .linkGlobalTypeCollector()
                            .linkInternalTypeCollector()
                            .linkKeywordCollector()
                            .linkPrimitiveTypeCollector();
                }
            } else {
                builder.linkModifierCollector(INTERFACE);
            }
        } else {
            throw new RuntimeException("Cannot find location in the interface declaration."); //NOI18N
        }
        return builder.build();
    }

    @Override
    public CodeFragmentInsertVisitor getCodeFragmentInsertVisitor() {
        return new InterfaceCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        return null;
    }
}
