/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import nb.java.abbreviator.constants.ConstantDataManager;
import nb.java.abbreviator.exception.NotFoundException;
import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;

/**
 *
 * @author Arthur Sadykov
 */
public class AbbrevDetection implements KeyListener, DocumentListener {

    private static final Logger LOG = Logger.getLogger(AbbrevDetection.class.getName());
    private final JTextComponent component;
    private final Document document;
    private final StringBuffer abbrevChars;
    private int abbrevEndPosition;
    private final Acceptor resetAcceptor;
    private JavaSourceHelper helper;

    private AbbrevDetection(JTextComponent component) {
        this.component = component;
        this.document = component.getDocument();
        this.abbrevChars = new StringBuffer();
        this.abbrevEndPosition = -1;
        this.resetAcceptor = AcceptorFactory.SPACE_NL;
    }

    public static AbbrevDetection get(JTextComponent component) {
        AbbrevDetection abbrevDetection = (AbbrevDetection) component.getClientProperty(AbbrevDetection.class);
        if (abbrevDetection == null) {
            abbrevDetection = new AbbrevDetection(component);
            component.putClientProperty(AbbrevDetection.class, abbrevDetection);
        }
        abbrevDetection.getComponent().addKeyListener(abbrevDetection);
        abbrevDetection.getDocument().addDocumentListener(abbrevDetection);
        if (abbrevDetection.getHelper() == null) {
            abbrevDetection.setHelper(new JavaSourceHelper(component.getDocument()));
        }
        return abbrevDetection;
    }

    public static synchronized void remove(JTextComponent component) {
        AbbrevDetection ad = (AbbrevDetection) component.getClientProperty(AbbrevDetection.class);
        if (ad != null) {
            if (ad.getComponent() != component) {
                throw new IllegalArgumentException("Wrong component: AbbrevDetection.component=" + ad.getComponent()
                        + ", component=" + component);
            }
            ad.uninstall();
            component.putClientProperty(AbbrevDetection.class, null);
        }
    }

    private JavaSourceHelper getHelper() {
        return helper;
    }

    private void setHelper(JavaSourceHelper helper) {
        this.helper = helper;
    }

    private JTextComponent getComponent() {
        return component;
    }

    private Document getDocument() {
        return document;
    }

    public int getAbbrevEndPosition() {
        return abbrevEndPosition;
    }

    private void uninstall() {
        helper = null;
        getComponent().removeKeyListener(this);
        getDocument().removeDocumentListener(this);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        if (isSpaceTyped(event)) {
            String abbreviation = getBufferContent();
            if (abbreviation.isEmpty() || abbrevEndPosition < 0) {
                return;
            }
            helper.setTypedAbbreviation(abbreviation);
            helper.collectLocalElements(abbrevEndPosition - abbreviation.length());
            if (abbreviation.contains(".")) {
                String expressionAbbreviation = abbreviation.substring(0, abbreviation.indexOf('.'));
                String identifierAbbreviation = abbreviation.substring(abbreviation.indexOf('.') + 1);
                List<Element> elements = helper.getElementsByAbbreviation(expressionAbbreviation);
                if (!elements.isEmpty()) {
                    if (!insertMethodSelection(elements, identifierAbbreviation, event)) {
                        insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation, event);
                    }
                } else {
                    insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation, event);
                }
            } else {
                if (!insertLocalElement(helper.getElementsByAbbreviation(abbreviation), event)) {
                    insertSelectionForMethodDefinedInCurrentOrSuperclass(abbreviation, event);
                }
            }
            resetAbbrevChars();
        }
    }

    private boolean isSpaceTyped(KeyEvent event) {
        if (event.getModifiersEx() != 0) {
            return false;
        }
        char keyChar = event.getKeyChar();
        return keyChar == ' ';
    }

    public String getBufferContent() {
        synchronized (abbrevChars) {
            return abbrevChars.toString();
        }
    }

    private boolean insertMethodSelection(List<Element> elements, String methodAbbreviation, KeyEvent event) {
        String abbreviation = getBufferContent();
        int endPosition = abbrevEndPosition;
        boolean succeed = false;
        try {
            removeAbbreviationFromDocument(endPosition - abbreviation.length(), abbreviation);
            for (int i = 0; i < elements.size(); i++) {
                succeed = insertMethodSelection(elements, methodAbbreviation);
                if (succeed) {
                    event.consume();
                    break;
                }
            }
        } catch (NotFoundException ex) {
            LOG.log(Level.INFO, null, ex);
        } finally {
            if (!succeed) {
                insertAbbreviationToDocument(endPosition - abbreviation.length(), abbreviation);
            }
        }
        return succeed;
    }

    private boolean insertLocalElement(List<Element> elements, KeyEvent event) {
        if (elements.isEmpty()) {
            return false;
        }
        String abbreviation = getBufferContent();
        int endPosition = abbrevEndPosition;
        boolean succeed = false;
        try {
            removeAbbreviationFromDocument(endPosition - abbreviation.length(), abbreviation);
            document.insertString(endPosition - abbreviation.length(), elements.get(0).getSimpleName().toString(), null);
            succeed = true;
            event.consume();
        } catch (BadLocationException ex) {
            LOG.log(Level.INFO, null, ex);
        } finally {
            if (!succeed) {
                insertAbbreviationToDocument(endPosition - abbreviation.length(), abbreviation);
            }
        }
        return succeed;
    }

    private void removeAbbreviationFromDocument(int position, String abbreviation) {
        requireNonNull(abbreviation, () ->
                String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "abbreviation"));
        if (position < 0) {
            return;
        }
        try {
            document.remove(position, abbreviation.length());
        } catch (BadLocationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private boolean insertMethodSelection(List<Element> elements, String methodAbbreviation) throws NotFoundException {
        return helper.insertCallToMethod(elements, methodAbbreviation);
    }

    private void insertAbbreviationToDocument(int position, String abbreviation) {
        if (position < 0) {
            return;
        }
        try {
            document.insertString(position, abbreviation, null);
        } catch (BadLocationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private boolean insertStaticMethodSelection(String expressionAbbreviation, String identifierAbbreviation,
            KeyEvent event) {
        String abbreviation = getBufferContent();
        int endPosition = abbrevEndPosition;
        List<TypeElement> typeElements = helper.getTypeElementsByAbbreviation(expressionAbbreviation);
        boolean succeed = false;
        try {
            removeAbbreviationFromDocument(endPosition - abbreviation.length(), abbreviation);
            for (int i = 0; i < typeElements.size(); i++) {
                succeed = insertStaticMethodSelection(typeElements, identifierAbbreviation);
                if (succeed) {
                    event.consume();
                    break;
                }
            }
        } catch (NotFoundException ex) {
            LOG.log(Level.INFO, null, ex);
        } finally {
            if (!succeed) {
                insertAbbreviationToDocument(endPosition - abbreviation.length(), abbreviation);
            }
        }
        return succeed;
    }

    private boolean insertStaticMethodSelection(List<TypeElement> elements, String methodAbbreviation)
            throws NotFoundException {
        return helper.insertStaticMethodSelection(elements, methodAbbreviation);
    }

    private boolean insertSelectionForMethodDefinedInCurrentOrSuperclass(String methodAbbreviation, KeyEvent event) {
        String abbreviation = getBufferContent();
        int endPosition = abbrevEndPosition;
        boolean succeed = false;
        try {
            removeAbbreviationFromDocument(endPosition - abbreviation.length(), abbreviation);
            succeed = helper.insertSelectionForMethodInCurrentOrSuperclass(methodAbbreviation);
            if (succeed) {
                event.consume();
            }
        } catch (NotFoundException ex) {
            LOG.log(Level.INFO, null, ex);
        } finally {
            if (!succeed) {
                insertAbbreviationToDocument(endPosition - abbreviation.length(), abbreviation);
            }
        }
        return succeed;
    }

    @Override
    public void keyPressed(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    private void resetAbbrevChars() {
        synchronized (abbrevChars) {
            abbrevChars.setLength(0);
        }
        abbrevEndPosition = -1;
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        if (DocumentUtilities.isTypingModification(event.getDocument())) {
            int offset = event.getOffset();
            int length = event.getLength();
            try {
                processTypedText(offset, document.getText(offset, length));
            } catch (BadLocationException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            resetAbbrevChars();
        }
    }

    private void processTypedText(int offset, String typedText) throws BadLocationException {
        requireNonNull(typedText, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "typedText"));
        if (offset != abbrevEndPosition) {
            resetAbbrevChars();
        }
        if (isNotWhitespace(offset) && isAccepted(DocumentUtilities.getText(document, offset, 1).charAt(0))) {
            appendToBuffer(typedText);
            setAbbrevEndPosition(offset + typedText.length());
        }
    }

    private boolean isAccepted(char character) {
        if (abbrevChars.length() == 0) {
            return !ConstantDataManager.FORBIDDEN_FIRST_CHARS.contains(character);
        }
        return true;
    }

    private boolean isNotWhitespace(int offset) throws BadLocationException {
        return !resetAcceptor.accept(DocumentUtilities.getText(document, offset, 1).charAt(0));
    }

    private void appendToBuffer(String typedText) {
        requireNonNull(typedText, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "typedText"));
        synchronized (abbrevChars) {
            abbrevChars.append(typedText);
        }
    }

    private void setAbbrevEndPosition(int abbrevEndPosition) {
        this.abbrevEndPosition = abbrevEndPosition;
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        if (DocumentUtilities.isTypingModification(event.getDocument())) {
            int offset = event.getOffset();
            int length = event.getLength();
            removeAbbrevText(offset, length);
        } else {
            resetAbbrevChars();
        }
    }

    private void removeAbbrevText(int offset, int removeLength) {
        synchronized (abbrevChars) {
            if (abbrevEndPosition != -1) {
                if (offset == abbrevEndPosition - 1 && abbrevChars.length() >= removeLength) {
                    removeLastCharactersFromBuffer(removeLength);
                    setAbbrevEndPosition(offset);
                } else {
                    resetAbbrevChars();
                }
            }
        }
    }

    private void removeLastCharactersFromBuffer(int count) {
        if (count < 0 || count > abbrevChars.length()) {
            throw new IllegalArgumentException(ConstantDataManager.INVALID_CHARS_COUNT);
        }
        synchronized (abbrevChars) {
            if (!getBufferContent().isEmpty()) {
                abbrevChars.setLength(abbrevChars.length() - count);
            }
        }
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
    }
}
