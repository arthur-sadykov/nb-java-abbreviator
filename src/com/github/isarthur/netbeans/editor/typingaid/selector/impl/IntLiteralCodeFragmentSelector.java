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
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class IntLiteralCodeFragmentSelector extends AbstractCodeFragmentSelector {

    @Override
    public String getTag() {
        return ConstantDataManager.SECOND_INT_LITERAL_TAG;
    }

    @Override
    public void select(ModificationResult modificationResult, JTextComponent component) {
        int[] span = modificationResult.getSpan(getTag());
        JavaSource javaSource = JavaSourceInitializeHandler.getJavaSourceForDocument(component.getDocument());
        try {
            javaSource.runUserActionTask(controller -> {
                JavaSourceInitializeHandler.moveStateToParsedPhase(controller);
                TokenSequence<?> tokenSequence = controller.getTokenHierarchy().tokenSequence();
                tokenSequence.move(span[0]);
                while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.INT_LITERAL) {
                }
                while (tokenSequence.moveNext() && tokenSequence.token().id() != JavaTokenId.INT_LITERAL) {
                }
                Token<?> token = tokenSequence.token();
                if (token != null && token.id() == JavaTokenId.INT_LITERAL) {
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
