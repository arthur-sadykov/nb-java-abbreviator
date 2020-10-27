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
package com.github.isarthur.netbeans.editor.typingaid;

import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import junit.framework.Test;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author: Arthur Sadykov
 */
public class JavaAbbreviationHandlerTest extends NbTestCase {

    private static final String JAVA_MIME_TYPE = "text/x-java";
    private static final String MIME_TYPE = "mimeType";
    private static final String JAVA_CLUSTER = "java";
    private static final String IDE_CLUSTER = "ide";
    private static final String EXTIDE_CLUSTER = "extide";
    private static final String TEST_FILE = "Test.java";
    private JavaAbbreviationHandler handler;
    private JavaAbbreviation abbreviation;
    private JEditorPane editor;
    private FileObject testFile;
    private Document document;

    public JavaAbbreviationHandlerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(JavaAbbreviationHandlerTest.class)
                .clusters(EXTIDE_CLUSTER)
                .clusters(IDE_CLUSTER)
                .clusters(JAVA_CLUSTER)
                .gui(false)
                .suite();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        testFile = FileUtil.toFileObject(getWorkDir()).createData(TEST_FILE);
        EditorKit kit = new NbEditorKit();
        editor = new JEditorPane();
        editor.setEditorKit(kit);
        document = editor.getDocument();
        document.putProperty(Document.StreamDescriptionProperty, testFile);
        document.putProperty(MIME_TYPE, JAVA_MIME_TYPE);
        document.putProperty(Language.class, JavaTokenId.language());
        document.putProperty(JavaSource.class, new WeakReference<>(JavaSource.forFileObject(testFile)));
        JavaSourceHelper helper = new JavaSourceHelper(editor);
        handler = new JavaAbbreviationHandler(helper);
        abbreviation = JavaAbbreviation.getInstance();
    }

    public void testShouldSuggestCompletionForParameterName() throws IOException {
        doAbbreviationInsert(
                "nos",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        numberOfSpaces\n"
                + "    }\n"
                + "}",
                Arrays.asList("numberOfSpaces"));
    }

    public void testShouldSuggestCompletionForFieldName() throws IOException {
        doAbbreviationInsert(
                "bn",
                "public class Test {\n"
                + "    private String branchName;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    private String branchName;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        branchName\n"
                + "    }\n"
                + "}",
                Arrays.asList("branchName"));
    }

    public void testShouldSuggestCompletionForLocalVariableName() throws IOException {
        doAbbreviationInsert(
                "ac",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String applicationContext = \"\";\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String applicationContext = \"\";\n"
                + "        applicationContext\n"
                + "    }\n"
                + "}",
                Arrays.asList("applicationContext"));
    }

    public void testShouldSuggestCompletionForMultipleMatchesOfLocalElements() throws IOException {
        doAbbreviationInsert(
                "nos",
                "public class Test {\n"
                + "    private int newObjectState;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String networkOperatingSystem = \"\";\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    private int newObjectState;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String networkOperatingSystem = \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("newObjectState", "numberOfSpaces", "networkOperatingSystem"));
    }

    public void testShouldSuggestCompletionForMethodInvocation() throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean b = branchName.isEmpty();\n"
                + "        \n"
                + "    }\n"
                + "}");
    }

    public void testWhenMethodInvocationIsPartOfIfConditionThenSuggestRightSideOfStatementWithoutSemicolon()
            throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (|) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if () {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}");
    }

    public void testWhenMethodInvocationIsPartOfWhileConditionThenSuggestRightSideOfStatementWithoutSemicolon()
            throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        while (|) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        while () {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        while (branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}");
    }

    public void testWhenMethodInvocationIsPartOfConditionalAndThenSuggestRightSideOfStatementWithoutSemicolon()
            throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && ) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}");
    }

    public void testWhenMethodInvocationIsPartOfLogicalComplementThenSuggestRightSideOfStatementWithoutSemicolon()
            throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && !|) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && !) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && !branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}");
    }

    public void testShouldSuggestCompletionForChainedMethodInvocation() throws IOException {
        doAbbreviationInsert(
                "ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        branchName.|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        branchName.isEmpty();\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        branchName.;\n"
                + "    }\n"
                + "}");
    }

    public void testShouldSuggestCompletionForChainedMethodInvocationUsedAsMethodArgument() throws IOException {
        doAbbreviationInsert(
                "ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        System.out.println(branchName.|);\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        System.out.println(branchName.isEmpty());\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        System.out.println(branchName.);\n"
                + "    }\n"
                + "}");
    }

    public void testShouldSuggestCompletionForChainedMethodInvocationUsedAsVariableInitializer() throws IOException {
        doAbbreviationInsert(
                "ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean empty = branchName.|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean empty = branchName.isEmpty();\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
        assertTestFileText(
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean empty = branchName.;\n"
                + "    }\n"
                + "}");
    }

    private void doAbbreviationInsert(String abbrev, String code, String golden, List<String> proposals)
            throws IOException {
        int caretOffset = code.indexOf('|');
        String text = code.substring(0, caretOffset) + code.substring(caretOffset + 1);
        editor.setText(text);
        editor.setCaretPosition(caretOffset);
        try ( OutputStream out = testFile.getOutputStream();  Writer writer = new OutputStreamWriter(out)) {
            writer.append(text);
        }
        abbreviation.setStartOffset(caretOffset);
        for (int i = 0; i < abbrev.length(); i++) {
            abbreviation.append(abbrev.charAt(i));
        }
        List<CodeFragment> codeFragments = handler.process(abbreviation);
        assertEquals(proposals.size(), codeFragments.size());
        for (int i = 0; i < codeFragments.size(); i++) {
            assertTrue(proposals.contains(codeFragments.get(i).toString()));
        }
        assertEquals(golden, editor.getText());
    }

    private void assertTestFileText(String text) throws IOException {
        assertEquals(text, testFile.asText());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        abbreviation.reset();
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }
}
