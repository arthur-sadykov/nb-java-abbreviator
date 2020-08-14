/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import junit.framework.Test;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author: Arthur Sadykov
 */
public class AbbrevDetectionTest extends NbTestCase {

    private final String LOCAL_VARIABLE_NAME = "localVariableName";
    private final String ARGS = "args";
    private final String INDEX = "index";
    private AbbrevDetection abbrevDetection;
    private Document document;
    private JEditorPane editorPane;
    private int caretPosition;
    private FileObject testFile;

    public AbbrevDetectionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(AbbrevDetectionTest.class)
                .clusters("extide")
                .clusters("ide")
                .clusters("java")
                .gui(false)
                .suite();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        testFile = FileUtil.toFileObject(getWorkDir()).createData("Test.java");
        editorPane = new JEditorPane();
        document = editorPane.getDocument();
        String code =
                "public class Test {\n"
                + "    private String " + LOCAL_VARIABLE_NAME + ";\n"
                + "    private void t(double... " + ARGS + ") {\n"
                + "        int " + INDEX + " = 0;\n"
                + "        |\n"
                + "    }\n"
                + "}";
        caretPosition = code.indexOf("|");
        String text = code.substring(0, caretPosition).concat(code.substring(caretPosition + 1));
        try (OutputStream out = testFile.getOutputStream(); Writer w = new OutputStreamWriter(out)) {
            w.append(text);
        }
        JavaSource javaSource = JavaSource.forFileObject(testFile);
        document.putProperty(JavaSource.class, new WeakReference<Object>(javaSource));
        document.putProperty(Document.StreamDescriptionProperty, testFile);
        document.putProperty(Language.class, JavaTokenId.language());
        document.putProperty("mimeType", "text/x-java");
        abbrevDetection = AbbrevDetection.get(editorPane);
        document.insertString(0, text, null);
        editorPane.setCaretPosition(caretPosition);
        DocumentUtilities.setTypingModification(document, true);
    }

    public void testShouldAppendAbbreviationToBuffer() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, "b", null);
        document.insertString(caretPosition + 2, "c", null);
        document.insertString(caretPosition + 3, ".", null);
        document.insertString(caretPosition + 4, "m", null);
        document.insertString(caretPosition + 5, "+", null);
        assertEquals("Expected value was not equal to actual!", "abc.m+", abbrevDetection.getBufferContent());
    }

    public void testShouldStoreAbbreviationEndPosition() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, "b", null);
        document.insertString(caretPosition + 2, "c", null);
        document.insertString(caretPosition + 3, ".", null);
        document.insertString(caretPosition + 4, "m", null);
        document.insertString(caretPosition + 5, "+", null);
        assertEquals("Expected value was not equal to actual!", caretPosition + 6, abbrevDetection
                .getAbbrevEndPosition());
    }

    public void testShouldClearAbbreviationBufferWhenItIsNotTypingModification() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, " ", null);
        document.insertString(caretPosition + 2, ".", null);
        DocumentUtilities.setTypingModification(document, false);
        document.insertString(caretPosition + 3, "m", null);
        document.insertString(caretPosition + 4, "+", null);
        assertTrue("Abbreviation buffer should be empty!", abbrevDetection.getBufferContent().isEmpty());
    }

    public void testShouldResetAbbreviationEndPositionWhenItIsNotTypingModification() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, " ", null);
        document.insertString(caretPosition + 2, ".", null);
        DocumentUtilities.setTypingModification(document, false);
        document.insertString(caretPosition + 3, "m", null);
        document.insertString(caretPosition + 4, "+", null);
        assertEquals("Expected value was not equal to actual!", -1, abbrevDetection.getAbbrevEndPosition());
    }

    public void testShouldRemoveAbbreviationFromBuffer() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, "b", null);
        document.insertString(caretPosition + 2, ".", null);
        document.insertString(caretPosition + 3, "m", null);
        document.insertString(caretPosition + 4, "+", null);
        document.remove(caretPosition + 4, 1);
        assertEquals("Expected value was not equal to actual!", "ab.m", abbrevDetection.getBufferContent());
        document.remove(caretPosition + 3, 1);
        assertEquals("Expected value was not equal to actual!", "ab.", abbrevDetection.getBufferContent());
        document.remove(caretPosition + 2, 1);
        assertEquals("Expected value was not equal to actual!", "ab", abbrevDetection.getBufferContent());
        document.remove(caretPosition + 1, 1);
        assertEquals("Expected value was not equal to actual!", "a", abbrevDetection.getBufferContent());
        document.remove(caretPosition, 1);
        assertEquals("Expected value was not equal to actual!", "", abbrevDetection.getBufferContent());
    }

    public void testShouldUpdateAbbreviationPositionWhenRemovingAbbreviation() throws BadLocationException {
        document.insertString(caretPosition, "a", null);
        document.insertString(caretPosition + 1, "b", null);
        document.insertString(caretPosition + 2, ".", null);
        document.insertString(caretPosition + 3, "m", null);
        document.insertString(caretPosition + 4, "+", null);
        document.remove(caretPosition + 4, 1);
        assertEquals("Expected value was not equal to actual!", caretPosition + 4, abbrevDetection
                .getAbbrevEndPosition());
        document.remove(caretPosition + 3, 1);
        assertEquals("Expected value was not equal to actual!", caretPosition + 3, abbrevDetection
                .getAbbrevEndPosition());
        document.remove(caretPosition + 2, 1);
        assertEquals("Expected value was not equal to actual!", caretPosition + 2, abbrevDetection
                .getAbbrevEndPosition());
        document.remove(caretPosition + 1, 1);
        assertEquals("Expected value was not equal to actual!", caretPosition + 1, abbrevDetection
                .getAbbrevEndPosition());
        document.remove(caretPosition, 1);
        assertEquals("Expected value was not equal to actual!", caretPosition, abbrevDetection.getAbbrevEndPosition());
    }
}
