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
package com.github.isarthur.netbeans.editor.typingaid.interceptor;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.abbreviation.impl.JavaAbbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.impl.JavaCodeFragmentCollectAndInsertHandler;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaTypedTextInterceptor implements TypedTextInterceptor {

    private final Acceptor resetAcceptor;
    private JavaCodeFragmentCollectAndInsertHandler handler;
    private final Abbreviation abbreviation;
    private List<CodeFragment> codeFragments;
    private int caretPosition;

    private JavaTypedTextInterceptor() {
        this.resetAcceptor = AcceptorFactory.SPACE_NL;
        this.abbreviation = Lookup.getDefault().lookup(JavaAbbreviation.class);
    }

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        if (handler == null) {
            handler = new JavaCodeFragmentCollectAndInsertHandler(context.getComponent());
        } else {
            if (context.getDocument() != handler.getDocument()) {
                handler = new JavaCodeFragmentCollectAndInsertHandler(context.getComponent());
            }
        }
        processTypedCharacter(context);
        caretPosition = context.getComponent().getCaretPosition();
        return false;
    }

    private void processTypedCharacter(Context context) throws BadLocationException {
        char typedCharacter = context.getText().charAt(0);
        int offset = context.getOffset();
        Document document = context.getDocument();
        if (offset != abbreviation.getEndOffset()) {
            abbreviation.reset();
        }
        if (isNotWhitespace(typedCharacter)) {
            if (isCharacterAccepted(typedCharacter)) {
                abbreviation.append(typedCharacter);
                abbreviation.setStartOffset(offset - abbreviation.length() + 1);
            }
            codeFragments = Collections.emptyList();
        } else {
            if (abbreviation.isEmpty()) {
                return;
            }
            removeAbbreviationFromDocument(abbreviation.getStartOffset(), abbreviation.getEndOffset(), document);
            codeFragments = handler.process(abbreviation);
        }
    }

    @Override
    public void insert(MutableContext context) throws BadLocationException {
        char typedCharacter = context.getText().charAt(0);
        if (isWhitespace(typedCharacter)) {
            if (abbreviation.isEmpty()) {
                return;
            }
            if (codeFragments.isEmpty()) {
                context.setText(abbreviation.getContent() + " ", abbreviation.length() + 1); //NOI18N
            } else {
                context.setText("", 0);
            }
            abbreviation.reset();
        }
    }

    private boolean isNotWhitespace(char typedCharacter) {
        return !resetAcceptor.accept(typedCharacter);
    }

    private boolean isWhitespace(char typedCharacter) {
        return resetAcceptor.accept(typedCharacter);
    }

    private boolean isCharacterAccepted(char typedCharacter) {
        if (abbreviation.isEmpty()) {
            return !ConstantDataManager.FORBIDDEN_FIRST_CHARS.contains(typedCharacter);
        }
        return true;
    }

    public String getBufferContent() {
        return abbreviation.getContent();
    }

    private void removeAbbreviationFromDocument(int startOffset, int endOffset, Document document) {
        if (startOffset < document.getStartPosition().getOffset() || startOffset > document.getEndPosition().getOffset()) {
            return;
        }
        if (endOffset < document.getStartPosition().getOffset() || endOffset > document.getEndPosition().getOffset()) {
            return;
        }
        try {
            document.remove(startOffset, endOffset - startOffset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void afterInsert(Context context) throws BadLocationException {
        if (codeFragments != null && !codeFragments.isEmpty()) {
            if (context.getText().isEmpty()) {
                JTextComponent component = context.getComponent();
                component.setCaretPosition(caretPosition);
            }
        }
    }

    @Override
    public void cancelled(Context context) {
    }

    @MimeRegistration(mimeType = "text/x-java", service = TypedTextInterceptor.Factory.class)
    public static class FactoryImpl implements TypedTextInterceptor.Factory {

        @Override
        public TypedTextInterceptor createTypedTextInterceptor(MimePath mimePath) {
            return new JavaTypedTextInterceptor();
        }
    }
}
