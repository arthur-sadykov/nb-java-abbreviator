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
package com.github.isarthur.netbeans.editor.typingaid.selector.impl;

import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.selector.api.AbstractCodeFragmentSelector;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceInitializeHandler;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import static org.netbeans.api.java.lexer.JavaTokenId.CHAR_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.DOUBLE_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.FALSE;
import static org.netbeans.api.java.lexer.JavaTokenId.FLOAT_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.IDENTIFIER;
import static org.netbeans.api.java.lexer.JavaTokenId.INT_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.LONG_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.MULTILINE_STRING_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.NULL;
import static org.netbeans.api.java.lexer.JavaTokenId.STRING_LITERAL;
import static org.netbeans.api.java.lexer.JavaTokenId.THIS;
import static org.netbeans.api.java.lexer.JavaTokenId.TRUE;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class IdentifierCodeFragmentSelector extends AbstractCodeFragmentSelector {

    @Override
    public String getTag() {
        return ConstantDataManager.ARGUMENT_TAG;
    }

    @Override
    public void select(ModificationResult modificationResult, JTextComponent component) {
        JavaSource javaSource = JavaSourceInitializeHandler.getJavaSourceForDocument(component.getDocument());
        try {
            javaSource.runUserActionTask(controller -> {
                JavaSourceInitializeHandler.moveStateToParsedPhase(controller);
                TokenSequence<?> tokenSequence = controller.getTokenHierarchy().tokenSequence();
                int[] span = modificationResult.getSpan(getTag());
                boolean argumentSpanTagFound = true;
                boolean firstIdentifierOrLiteralSpanTagFound = false;
                boolean secondIdentifierOrLiteralSpanTagFound = false;
                boolean methodNameSpanTagFound = false;
                if (span == null) {
                    argumentSpanTagFound = false;
                    span = modificationResult.getSpan(ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG);
                    if (span == null) {
                        firstIdentifierOrLiteralSpanTagFound = false;
                        span = modificationResult.getSpan(ConstantDataManager.SECOND_IDENTIFIER_OR_LITERAL_TAG);
                        if (span == null) {
                            secondIdentifierOrLiteralSpanTagFound = false;
                            span = modificationResult.getSpan(ConstantDataManager.METHOD_NAME_TAG);
                            if (span == null) {
                                methodNameSpanTagFound = false;
                                span = modificationResult.getSpan(ConstantDataManager.VARIABLE_NAME_TAG);
                                if (span == null) {
                                    return;
                                }
                            } else {
                                methodNameSpanTagFound = true;
                            }
                        } else {
                            secondIdentifierOrLiteralSpanTagFound = true;
                        }
                    } else {
                        firstIdentifierOrLiteralSpanTagFound = true;
                    }
                }
                tokenSequence.move(span[0]);
                Set<? extends TokenId> targetTokeIds = EnumSet.of(CHAR_LITERAL, DOUBLE_LITERAL, FALSE, FLOAT_LITERAL,
                        IDENTIFIER, INT_LITERAL, LONG_LITERAL, MULTILINE_STRING_LITERAL, NULL, STRING_LITERAL, THIS, TRUE);
                if (argumentSpanTagFound) {
                    while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.LPAREN) {
                    }
                    tokenSequence.moveNext();
                } else if (firstIdentifierOrLiteralSpanTagFound) {
                    while (tokenSequence.moveNext() && !targetTokeIds.contains(tokenSequence.token().id())) {
                    }
                } else if (secondIdentifierOrLiteralSpanTagFound) {
                    while (tokenSequence.moveNext() && !targetTokeIds.contains(tokenSequence.token().id())) {
                    }
                    while (tokenSequence.moveNext() && !targetTokeIds.contains(tokenSequence.token().id())) {
                    }
                } else if (methodNameSpanTagFound) {
                    while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.LPAREN) {
                    }
                    Token<?> token = tokenSequence.token();
                    if (token != null && token.id() == JavaTokenId.LPAREN) {
                        while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                        }
                        token = tokenSequence.token();
                        if (token == null) {
                            return;
                        }
                        if (token.id() != JavaTokenId.IDENTIFIER) {
                            return;
                        }
                    }
                } else {
                    while (tokenSequence.moveNext()) {
                        if (tokenSequence.token().id() == JavaTokenId.SEMICOLON
                                || tokenSequence.token().id() == JavaTokenId.COMMA
                                || tokenSequence.token().id() == JavaTokenId.RPAREN
                                || tokenSequence.token().id() == JavaTokenId.EQ) {
                            break;
                        }
                    }
                    Token<?> token = tokenSequence.token();
                    if (token != null
                            && (token.id() == JavaTokenId.SEMICOLON
                            || token.id() == JavaTokenId.COMMA
                            || token.id() == JavaTokenId.RPAREN
                            || tokenSequence.token().id() == JavaTokenId.EQ)) {
                        while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                        }
                        token = tokenSequence.token();
                        if (token == null || token.id() != JavaTokenId.IDENTIFIER) {
                            return;
                        }
                    }
                }
                Token<?> token = tokenSequence.token();
                if (token != null && targetTokeIds.contains(token.id())) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            component.setCaretPosition(tokenSequence.offset());
                            component.moveCaretPosition(tokenSequence.offset() + tokenSequence.token().length());
                        } catch (IllegalArgumentException e) {
                        }
                    });
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
