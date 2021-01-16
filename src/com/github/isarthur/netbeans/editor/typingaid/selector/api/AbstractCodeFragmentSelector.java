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
package com.github.isarthur.netbeans.editor.typingaid.selector.api;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.ModificationResult;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractCodeFragmentSelector implements CodeFragmentSelector, Taggable {

    @Override
    public void select(ModificationResult modificationResult, JTextComponent component) {
        int[] span = modificationResult.getSpan(getTag());
        if (span == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                component.setCaretPosition(span[0]);
                component.moveCaretPosition(span[1]);
            } catch (IllegalArgumentException e) {
            }
        });
    }
}
