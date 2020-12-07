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

import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import junit.framework.Test;
import static org.junit.Assert.assertArrayEquals;
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
public class KeywordCompletionTest extends NbTestCase {

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
    private boolean keyword;
    private boolean modifier;
    private boolean externalType;
    private boolean internalType;
    private boolean resourceVariable;
    private boolean exceptionParameter;
    private boolean enumConstant;
    private boolean parameter;
    private boolean field;
    private boolean localVariable;
    private boolean staticFieldAccess;
    private boolean localMethodInvocation;
    private boolean chainedMethodInvocation;
    private boolean staticMethodInvocation;
    private boolean methodInvocation;

    public KeywordCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(KeywordCompletionTest.class)
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
        storeSettings();
        setConfigurationForKeywordCompletion();
    }

    private void storeSettings() {
        methodInvocation = Settings.getSettingForMethodInvocation();
        staticMethodInvocation = Settings.getSettingForStaticMethodInvocation();
        chainedMethodInvocation = Settings.getSettingForChainedMethodInvocation();
        localMethodInvocation = Settings.getSettingForLocalMethodInvocation();
        staticFieldAccess = Settings.getSettingForStaticFieldAccess();
        localVariable = Settings.getSettingForLocalVariable();
        field = Settings.getSettingForField();
        parameter = Settings.getSettingForParameter();
        enumConstant = Settings.getSettingForEnumConstant();
        exceptionParameter = Settings.getSettingForExceptionParameter();
        resourceVariable = Settings.getSettingForResourceVariable();
        internalType = Settings.getSettingForInternalType();
        externalType = Settings.getSettingForExternalType();
        keyword = Settings.getSettingForKeyword();
        modifier = Settings.getSettingForModifier();
    }

    private void setConfigurationForKeywordCompletion() {
        Settings.setSettingForMethodInvocation(false);
        Settings.setSettingForStaticMethodInvocation(false);
        Settings.setSettingForChainedMethodInvocation(false);
        Settings.setSettingForLocalMethodInvocation(false);
        Settings.setSettingForStaticFieldAccess(false);
        Settings.setSettingForLocalVariable(false);
        Settings.setSettingForField(false);
        Settings.setSettingForParameter(false);
        Settings.setSettingForEnumConstant(false);
        Settings.setSettingForExceptionParameter(false);
        Settings.setSettingForResourceVariable(false);
        Settings.setSettingForInternalType(false);
        Settings.setSettingForExternalType(false);
        Settings.setSettingForKeyword(true);
        Settings.setSettingForModifier(false);
    }

    public void testAssertKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        assert true : \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("assert true : \"\";"));
    }

    public void testBreakKeywordCompletionInForLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("break;"));
    }

    public void testBreakKeywordCompletionInWhileLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("break;"));
    }

    public void testBreakKeywordCompletionInDoWhileLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            |\n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            break;\n"
                + "            \n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("break;"));
    }

    public void testBreakKeywordCompletionInSwitchStatement() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    int count = 10;\n"
                + "    void test() {\n"
                + "        switch (count) {\n"
                + "            case 0:\n"
                + "                count = 0;\n"
                + "                |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int count = 10;\n"
                + "    void test() {\n"
                + "        switch (count) {\n"
                + "            case 0:\n"
                + "                count = 0;\n"
                + "            break;\n"
                + "                \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("break;"));
    }

    public void testBreakKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testContinueKeywordCompletionInForLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testContinueKeywordCompletionInWhileLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testContinueKeywordCompletionInDoWhileLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            |\n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            \n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testContinueKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("class"));
    }

    public void testWhileKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "w",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("while (true) {" + System.lineSeparator() + "}"));
    }

    public void testDoWhileKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "        } while (true);\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("do {" + System.lineSeparator() + "} while (true);"));
    }

    public void testForKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("for (int i = 0; i < 10; i++) {" + System.lineSeparator() + "}"));
    }

    public void testTryKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("this", "throw", "try"));
    }

    public void testCatchKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } |catch (IndexOutOfBoundsException e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (IndexOutOfBoundsException e) {\n"
                + "        } catch (Exception e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList(" catch (Exception e) {" + System.lineSeparator() + "}"));
    }

    public void testFinallyKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } |catch (IndexOutOfBoundsException e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (IndexOutOfBoundsException e) {\n"
                + "        } finally {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("{" + System.lineSeparator() + "}"));
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
        assertNotNull(codeFragments);
        assertArrayEquals(proposals.toArray(), codeFragments.stream().map(fragment -> fragment.toString()).toArray());
        assertEquals(golden, testFile.asText());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        abbreviation.reset();
        revertSettings();
    }

    private void revertSettings() {
        Settings.setSettingForMethodInvocation(methodInvocation);
        Settings.setSettingForStaticMethodInvocation(staticMethodInvocation);
        Settings.setSettingForChainedMethodInvocation(chainedMethodInvocation);
        Settings.setSettingForLocalMethodInvocation(localMethodInvocation);
        Settings.setSettingForStaticFieldAccess(staticFieldAccess);
        Settings.setSettingForLocalVariable(localVariable);
        Settings.setSettingForField(field);
        Settings.setSettingForParameter(parameter);
        Settings.setSettingForEnumConstant(enumConstant);
        Settings.setSettingForExceptionParameter(exceptionParameter);
        Settings.setSettingForResourceVariable(resourceVariable);
        Settings.setSettingForInternalType(internalType);
        Settings.setSettingForExternalType(externalType);
        Settings.setSettingForKeyword(keyword);
        Settings.setSettingForModifier(modifier);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }
}
