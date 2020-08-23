/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import nb.java.abbreviator.constants.ConstantDataManager;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaTypedTextInterceptor implements TypedTextInterceptor {

    private final Acceptor resetAcceptor;
    private JavaAbbreviationHandler handler;
    private final Abbreviation abbreviation;
    private boolean succeed;
    private int caretPosition;

    private JavaTypedTextInterceptor() {
        this.resetAcceptor = AcceptorFactory.SPACE_NL;
        this.abbreviation = Abbreviation.getInstance();
    }

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        if (handler == null) {
            handler = new JavaAbbreviationHandler(new JavaSourceHelper(context.getDocument()));
        } else {
            if (context.getDocument() != handler.getDocument()) {
                handler = new JavaAbbreviationHandler(new JavaSourceHelper(context.getDocument()));
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
        if (offset != abbreviation.getEndPosition()) {
            abbreviation.reset();
        }
        if (isNotWhitespace(typedCharacter)) {
            if (isCharacterAccepted(typedCharacter)) {
                abbreviation.append(typedCharacter);
                abbreviation.setEndPosition(offset + 1);
            }
            succeed = true;
        } else {
            if (abbreviation.isEmpty()) {
                return;
            }
            removeAbbreviationFromDocument(abbreviation.getStartPosition(), abbreviation.getEndPosition(), document);
            succeed = handler.process(abbreviation);
        }
    }

    @Override
    public void insert(MutableContext context) throws BadLocationException {
        char typedCharacter = context.getText().charAt(0);
        if (isWhitespace(typedCharacter)) {
            if (abbreviation.isEmpty()) {
                return;
            }
            if (!succeed) {
                context.setText(abbreviation.getContent() + " ", abbreviation.length() + 1);
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
        if (succeed) {
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
