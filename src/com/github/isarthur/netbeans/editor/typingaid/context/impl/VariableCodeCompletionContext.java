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
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl.VariableCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class VariableCodeCompletionContext extends AbstractCodeCompletionContext {

    @Override
    protected CodeFragmentCollectorLinkerImpl getCodeFragmentCollectorLinker(CodeCompletionRequest request) {
        WorkingCopy workingCopy = request.getWorkingCopy();
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokenSequence = workingCopy.getTokenHierarchy().tokenSequence();
        tokenSequence.move(abbreviation.getStartOffset());
        while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        Token<?> token = tokenSequence.token();
        if (token != null && JavaSourceUtilities.isModifier(token.id())) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkModifierCollector(VARIABLE)
                    .build();
        }
        tokenSequence.move(abbreviation.getStartOffset());
        while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        token = tokenSequence.token();
        if (token != null && token.id() == JavaTokenId.EQ) {
            return CodeFragmentCollectorLinkerImpl.builder()
                    .linkNameCollector()
                    .build();
        }
        tokenSequence.move(abbreviation.getStartOffset());
        while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        token = tokenSequence.token();
        if (token != null
                && (token.id() == JavaTokenId.SEMICOLON
                || token.id() == JavaTokenId.COMMA
                || token.id() == JavaTokenId.RPAREN)) {
            tokenSequence.move(abbreviation.getStartOffset());
            while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            token = tokenSequence.token();
            if (token != null && token.id() == JavaTokenId.IDENTIFIER) {
                return CodeFragmentCollectorLinkerImpl.builder()
                        .linkNameCollector()
                        .build();
            }
        }
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
        return new VariableCodeFragmentInsertVisitor();
    }

    @Override
    public TypeMirror getType(CodeCompletionRequest request) {
        TreePath currentPath = request.getCurrentPath();
        VariableTree variableTree = (VariableTree) request.getCurrentTree();
        Tree type = variableTree.getType();
        TreePath path = TreePath.getPath(currentPath, type);
        Trees trees = request.getWorkingCopy().getTrees();
        return trees.getElement(path).asType();
    }
}
