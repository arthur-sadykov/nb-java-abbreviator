/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.lang.model.element.Element;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import junit.framework.Test;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author: Arthur Sadykov
 */
public class JavaSourceHelperTest extends NbTestCase {

    private final String LOCAL_VARIABLE_NAME = "localVariableName";
    private final String ARGS = "args";
    private final String INDEX = "index";
    private JavaSourceHelper helper;
    private Document document;
    private JEditorPane editorPane;
    private int caretPosition;
    private FileObject testFile;

    public JavaSourceHelperTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(JavaSourceHelperTest.class)
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
        helper = new JavaSourceHelper(document);
        helper.collectLocalElements(caretPosition);
        document.insertString(0, text, null);
    }

    public void testShouldReturnFieldsLocalVariablesAndParametersForScopeIdentifiedByCaretPosition() {
        List<Element> elements = helper.getLocalElements();
        assertEquals("Expected value was not equal to actual!", 3, elements.size());
        assertEquals("Expected value was not equal to actual!", ARGS, elements.get(0).getSimpleName().toString());
        assertEquals("Expected value was not equal to actual!", INDEX, elements.get(1).getSimpleName().toString());
        assertEquals("Expected value was not equal to actual!", LOCAL_VARIABLE_NAME, elements.get(2).getSimpleName()
                .toString());
    }

    public void testShouldGetElementsByAbbreviation() {
        List<Element> elements = helper.getElementsByAbbreviation("lvn");
        Element element = elements.get(0);
        assertEquals("Expected value was not equal to actual", LOCAL_VARIABLE_NAME, element.getSimpleName().toString());
        elements = helper.getElementsByAbbreviation("i");
        element = elements.get(0);
        assertEquals("Expected value was not equal to actual", INDEX, element.getSimpleName().toString());
        elements = helper.getElementsByAbbreviation("a");
        element = elements.get(0);
        assertEquals("Expected value was not equal to actual", ARGS, element.getSimpleName().toString());
    }
}
