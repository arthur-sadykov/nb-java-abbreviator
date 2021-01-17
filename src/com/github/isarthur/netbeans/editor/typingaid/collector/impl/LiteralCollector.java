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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.api.Literal;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.FalseLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.NullLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.TrueLiteral;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import static com.sun.source.tree.Tree.Kind.ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.EQUAL_TO;
import static com.sun.source.tree.Tree.Kind.METHOD_INVOCATION;
import static com.sun.source.tree.Tree.Kind.NEW_CLASS;
import static com.sun.source.tree.Tree.Kind.NOT_EQUAL_TO;
import static com.sun.source.tree.Tree.Kind.PARENTHESIZED;
import static com.sun.source.tree.Tree.Kind.RETURN;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class LiteralCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        Abbreviation abbreviation = request.getAbbreviation();
        switch (request.getCurrentKind()) {
            case CONDITIONAL_EXPRESSION:
            case EQUAL_TO:
            case METHOD_INVOCATION:
            case NEW_CLASS:
            case NOT_EQUAL_TO:
            case PARENTHESIZED:
            case RETURN:
                collectLiterals(request);
                break;
            case ASSIGNMENT:
            case VARIABLE:
                TreeUtilities treeUtilities = request.getWorkingCopy().getTreeUtilities();
                TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(request.getCurrentPath().getLeaf());
                tokenSequence.moveStart();
                while (tokenSequence.moveNext()) {
                    if (tokenSequence.token().id() == JavaTokenId.EQ) {
                        break;
                    }
                }
                if (tokenSequence.token().id() == JavaTokenId.EQ) {
                    if (tokenSequence.offset() < abbreviation.getStartOffset()) {
                        while (tokenSequence.moveNext()) {
                            if (tokenSequence.token().id() != JavaTokenId.SEMICOLON
                                    && tokenSequence.token().id() != JavaTokenId.WHITESPACE) {
                                return;
                            }
                        }
                        collectLiterals(request);
                    }
                }
                break;
        }
        super.collect(request);
    }

    private void collectLiterals(CodeCompletionRequest request) {
        Set<Literal> literals = new HashSet<>(Arrays.asList(
                new FalseLiteral(),
                new NullLiteral(),
                new TrueLiteral()));
        List<CodeFragment> codeFragments = request.getCodeFragments();
        Abbreviation abbreviation = request.getAbbreviation();
        literals.stream()
                .filter(literal -> literal.isAbbreviationEqualTo(abbreviation.getIdentifier()))
                .forEach(codeFragments::add);
    }
}
