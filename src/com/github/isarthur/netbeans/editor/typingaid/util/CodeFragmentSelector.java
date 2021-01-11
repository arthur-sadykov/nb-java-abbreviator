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
package com.github.isarthur.netbeans.editor.typingaid.util;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Arthur Sadykov
 */
public class CodeFragmentSelector {

    private CodeFragmentSelector() {
    }

    public static void select(CodeFragment codeFragment, ModificationResult modificationResult, FileObject fileObject,
            JTextComponent component) {
        List<? extends ModificationResult.Difference> differences = modificationResult.getDifferences(fileObject);
        if (differences != null && !differences.isEmpty()) {
            ModificationResult.Difference difference =
                    getDifferenceClosestToCurrentContext(differences, component.getCaretPosition());
            switch (codeFragment.getKind()) {
                case ASSERT_KEYWORD:
                case DO_KEYWORD:
                case ELSE_KEYWORD:
                case IF_KEYWORD:
                case WHILE_KEYWORD:
                    selectToken(JavaTokenId.TRUE, component, difference);
                    break;
                case CASE_KEYWORD:
                    setCaretPosition(JavaTokenId.CASE, 1, component, difference);
                    break;
                case CATCH_KEYWORD:
                case CLASS_KEYWORD:
                case ENUM_KEYWORD:
                case INTERFACE_KEYWORD:
                case THROW_KEYWORD:
                case TRY_KEYWORD:
                case VOID_KEYWORD:
                    selectToken(JavaTokenId.IDENTIFIER, component, difference);
                    break;
                case BOOLEAN_PRIMITIVE_TYPE:
                case BYTE_PRIMITIVE_TYPE:
                case CHAR_PRIMITIVE_TYPE:
                case DOUBLE_PRIMITIVE_TYPE:
                case EXTERNAL_TYPE:
                case FLOAT_PRIMITIVE_TYPE:
                case GLOBAL_TYPE:
                case INT_PRIMITIVE_TYPE:
                case INTERNAL_TYPE:
                case LOCAL_METHOD_INVOCATION:
                case LONG_PRIMITIVE_TYPE:
                case NEW_KEYWORD:
                case NORMAL_METHOD_INVOCATION:
                case SHORT_PRIMITIVE_TYPE:
                case STATIC_METHOD_INVOCATION:
                    selectNameOrInvocationArgument(component, difference);
                    break;
                case FOR_KEYWORD:
                    selectForConditionLiteral(component, difference);
                    break;
                case IMPORT_KEYWORD:
                    setCaretPosition(JavaTokenId.IMPORT, 1, component, difference);
                    break;
                case RETURN_KEYWORD:
                    selectReturnExpression(component, difference);
                    break;
                case SWITCH_KEYWORD:
                    setCaretPosition(JavaTokenId.LPAREN, 0, component, difference);
                    break;
            }
        }
    }

    private static void selectForConditionLiteral(JTextComponent component, ModificationResult.Difference difference) {
        Document document = component.getDocument();
        document.render(() -> {
            TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(component.getDocument());
            TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
            tokenSequence.move(getDifferenceStartPosition(difference));
            boolean literalFound = false;
            while (tokenSequence.moveNext()) {
                if (tokenSequence.token().id() == JavaTokenId.INT_LITERAL
                        && tokenSequence.token().text().toString().equals("10")) { //NOI18N
                    literalFound = true;
                    break;
                }
            }
            if (literalFound) {
                int startPosition = tokenSequence.offset();
                int endPosition = startPosition + tokenSequence.token().length();
                SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
            }
        });
    }

    private static void selectNameOrInvocationArgument(
            JTextComponent component, ModificationResult.Difference difference) {
        Document document = component.getDocument();
        document.render(() -> {
            TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(component.getDocument());
            TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
            tokenSequence.move(getDifferenceStartPosition(difference));
            boolean whitespaceFound = false;
            boolean newKeywordFound = false;
            while (tokenSequence.moveNext()
                    && tokenSequence.token().id() != JavaTokenId.EQ
                    && tokenSequence.token().id() != JavaTokenId.LPAREN
                    && tokenSequence.token().id() != JavaTokenId.COMMA
                    && tokenSequence.token().id() != JavaTokenId.RPAREN
                    && tokenSequence.token().id() != JavaTokenId.SEMICOLON) {
                if (tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                    whitespaceFound = true;
                } else if (tokenSequence.token().id() == JavaTokenId.NEW) {
                    newKeywordFound = true;
                }
            }
            if (tokenSequence.token().id() == JavaTokenId.EQ) {
                while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                if (tokenSequence.token().id() == JavaTokenId.IDENTIFIER) {
                    int startPosition = tokenSequence.offset();
                    int endPosition = startPosition + tokenSequence.token().length();
                    SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
                }
            } else if (tokenSequence.token().id() == JavaTokenId.LPAREN) {
                if (whitespaceFound && !newKeywordFound) {
                    while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                    }
                    if (tokenSequence.token().id() == JavaTokenId.IDENTIFIER) {
                        int startPosition = tokenSequence.offset();
                        int endPosition = tokenSequence.offset() + tokenSequence.token().length();
                        SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
                    }
                } else {
                    while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                    }
                    if (tokenSequence.token().id() == JavaTokenId.RPAREN) {
                        return;
                    }
                    if (tokenSequence.token().id() != JavaTokenId.WHITESPACE) {
                        int startPosition = tokenSequence.offset();
                        while (tokenSequence.moveNext()) {
                            if (tokenSequence.token().id() == JavaTokenId.COMMA
                                    || tokenSequence.token().id() == JavaTokenId.RPAREN) {
                                tokenSequence.movePrevious();
                                break;
                            }
                        }
                        if (tokenSequence.token().id() != JavaTokenId.WHITESPACE
                                && tokenSequence.token().id() != JavaTokenId.LPAREN) {
                            int endPosition = tokenSequence.offset() + tokenSequence.token().length();
                            SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
                        }
                    }
                }
            } else if (tokenSequence.token().id() == JavaTokenId.COMMA
                    || tokenSequence.token().id() == JavaTokenId.RPAREN) {
                while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                if (tokenSequence.token().id() == JavaTokenId.IDENTIFIER) {
                    int startPosition = tokenSequence.offset();
                    int endPosition = tokenSequence.offset() + tokenSequence.token().length();
                    component.select(startPosition, endPosition);
                }
            } else if (tokenSequence.token().id() == JavaTokenId.SEMICOLON) {
                while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                }
                if (tokenSequence.token().id() != JavaTokenId.WHITESPACE) {
                    int startPosition = tokenSequence.offset();
                    int endPosition = startPosition + tokenSequence.token().length();
                    SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
                }
            }
        });
    }

    private static void selectToken(
            JavaTokenId tokenId, JTextComponent component, ModificationResult.Difference difference) {
        Document document = component.getDocument();
        document.render(() -> {
            TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(component.getDocument());
            TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
            tokenSequence.move(getDifferenceStartPosition(difference));
            while (tokenSequence.moveNext() && tokenSequence.token().id() != tokenId) {
            }
            if (tokenSequence.token().id() == tokenId) {
                int startPosition = tokenSequence.offset();
                int endPosition = startPosition + tokenSequence.token().length();
                SwingUtilities.invokeLater(() -> component.select(startPosition, endPosition));
            }
        });
    }

    private static void selectReturnExpression(JTextComponent component, ModificationResult.Difference difference) {
        Document document = component.getDocument();
        document.render(() -> {
            TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(component.getDocument());
            TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
            tokenSequence.move(getDifferenceStartPosition(difference));
            while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.RETURN) {
            }
            while (tokenSequence.moveNext() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            AtomicInteger startPosition = new AtomicInteger();
            AtomicInteger endPosition = new AtomicInteger();
            if (tokenSequence.token().id() != JavaTokenId.WHITESPACE) {
                startPosition.set(tokenSequence.offset());
                while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.SEMICOLON) {
                }
                if (tokenSequence.token().id() == JavaTokenId.SEMICOLON) {
                    while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
                    }
                    if (tokenSequence.token().id() != JavaTokenId.WHITESPACE) {
                        endPosition.set(tokenSequence.offset() + tokenSequence.token().length());
                        SwingUtilities.invokeLater(() -> component.select(startPosition.get(), endPosition.get()));
                    }
                }
            }
        });
    }

    private static void setCaretPosition(
            JavaTokenId tokenId, int offset, JTextComponent component, ModificationResult.Difference difference) {
        Document document = component.getDocument();
        document.render(() -> {
            TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(component.getDocument());
            TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
            tokenSequence.move(getDifferenceStartPosition(difference));
            while (tokenSequence.moveNext() && tokenSequence.token().id() != tokenId) {
            }
            if (tokenSequence.token().id() == tokenId) {
                int startPosition = tokenSequence.offset();
                int endPosition = startPosition + tokenSequence.token().length();
                SwingUtilities.invokeLater(() -> component.setCaretPosition(endPosition + offset));
            }
        });
    }

    private static ModificationResult.Difference getDifferenceClosestToCurrentContext(
            List<? extends ModificationResult.Difference> differences, int caretPosition) {
        int minDelta = Integer.MAX_VALUE;
        ModificationResult.Difference targetDifference = differences.get(0);
        for (ModificationResult.Difference difference : differences) {
            int differenceStartPosition = difference.getStartPosition().getOffset();
            int differenceEndPosition = difference.getEndPosition().getOffset();
            int delta = Math.min(
                    Math.abs(caretPosition - differenceStartPosition), Math.abs(caretPosition - differenceEndPosition));
            if (delta < minDelta) {
                minDelta = delta;
                targetDifference = difference;
            }
        }
        return targetDifference;
    }

    private static int getDifferenceStartPosition(ModificationResult.Difference difference) {
        return Math.min(difference.getStartPosition().getOffset(), difference.getEndPosition().getOffset());
    }
}
