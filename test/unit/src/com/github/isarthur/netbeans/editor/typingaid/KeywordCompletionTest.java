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

import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import junit.framework.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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
    private boolean primitiveType;
    private boolean externalType;
    private boolean internalType;
    private boolean importedType;
    private boolean samePackageType;
    private boolean resourceVariable;
    private boolean exceptionParameter;
    private boolean enumConstant;
    private boolean parameter;
    private boolean field;
    private boolean localVariable;
    private boolean staticFieldAccess;
    private boolean localMethodInvocation;
    private boolean chainedMethodInvocation;
    private boolean chainedFieldAccess;
    private boolean chainedEnumConstantAccess;
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
        SwingUtilities.invokeAndWait(() -> editor.setEditorKit(kit));
        document = editor.getDocument();
        document.putProperty(Document.StreamDescriptionProperty, testFile);
        document.putProperty(MIME_TYPE, JAVA_MIME_TYPE);
        document.putProperty(Language.class, JavaTokenId.language());
        document.putProperty(JavaSource.class, new WeakReference<>(JavaSource.forFileObject(testFile)));
        JavaSourceHelper helper = new JavaSourceHelper(editor);
        handler = new JavaAbbreviationHandler(helper);
        abbreviation = new JavaAbbreviation();
        storeSettings();
        setConfigurationForKeywordCompletion();
    }

    private void storeSettings() {
        staticMethodInvocation = Preferences.getStaticMethodInvocationFlag();
        staticFieldAccess = Preferences.getStaticFieldAccessFlag();
        methodInvocation = Preferences.getMethodInvocationFlag();
        chainedMethodInvocation = Preferences.getChainedMethodInvocationFlag();
        chainedFieldAccess = Preferences.getChainedFieldAccessFlag();
        chainedEnumConstantAccess = Preferences.getChainedEnumConstantAccessFlag();
        localMethodInvocation = Preferences.getLocalMethodInvocationFlag();
        localVariable = Preferences.getLocalVariableFlag();
        field = Preferences.getFieldFlag();
        parameter = Preferences.getParameterFlag();
        enumConstant = Preferences.getEnumConstantFlag();
        exceptionParameter = Preferences.getExceptionParameterFlag();
        resourceVariable = Preferences.getResourceVariableFlag();
        internalType = Preferences.getInternalTypeFlag();
        externalType = Preferences.getExternalTypeFlag();
        importedType = Preferences.getImportedTypeFlag();
        samePackageType = Preferences.getSamePackageTypeFlag();
        keyword = Preferences.getKeywordFlag();
        modifier = Preferences.getModifierFlag();
        primitiveType = Preferences.getPrimitiveTypeFlag();
    }

    private void setConfigurationForKeywordCompletion() {
        Preferences.setStaticMethodInvocationFlag(false);
        Preferences.setStaticFieldAccessFlag(false);
        Preferences.setMethodInvocationFlag(false);
        Preferences.setChainedMethodInvocationFlag(false);
        Preferences.setChainedFieldAccessFlag(false);
        Preferences.setChainedEnumConstantAccessFlag(false);
        Preferences.setLocalMethodInvocationFlag(false);
        Preferences.setLocalVariableFlag(false);
        Preferences.setFieldFlag(false);
        Preferences.setParameterFlag(false);
        Preferences.setEnumConstantFlag(false);
        Preferences.setExceptionParameterFlag(false);
        Preferences.setResourceVariableFlag(false);
        Preferences.setInternalTypeFlag(false);
        Preferences.setExternalTypeFlag(false);
        Preferences.setImportedTypeFlag(false);
        Preferences.setSamePackageTypeFlag(false);
        Preferences.setKeywordFlag(true);
        Preferences.setModifierFlag(false);
        Preferences.setPrimitiveTypeFlag(false);
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
                Collections.singletonList("assert"));
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
                Arrays.asList("break"));
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
                Arrays.asList("break"));
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
                Arrays.asList("break"));
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
                Collections.singletonList("break"));
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
                Collections.singletonList("while"));
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
                Collections.singletonList("do"));
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
                Collections.singletonList("for"));
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
                Arrays.asList("throw", "try"));
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
                Collections.singletonList("catch"));
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
                Collections.singletonList("finally"));
    }

    public void testThrowKeywordCompletion() throws IOException {
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
                Arrays.asList("throw", "try"));
    }

    public void testImplementsKeywordCompletionForClass() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test |{\n"
                + "}",
                "class Test implements  {\n"
                + "}",
                Collections.singletonList("implements"));
    }

    public void testImplementsKeywordCompletionForEnum() throws IOException {
        doAbbreviationInsert(
                "i",
                "enum Test |{\n"
                + "}",
                "enum Test implements  {\n"
                + "}",
                Collections.singletonList("implements"));
    }

    public void testInterfaceKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("interface"));
    }

    public void testInterfaceKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "i",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("interface"));
    }

    public void testInterfaceKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "i",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("interface"));
    }

    public void testInterfaceKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n",
                Arrays.asList("import", "interface"));
    }

    public void testClassKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("class"));
    }

    public void testClassKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "c",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("class"));
    }

    public void testClassKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "c",
                "enum Test {\n"
                + "    MIDDLE;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    MIDDLE;\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("class"));
    }

    public void testClassKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n"
                + "\n"
                + "class Class {\n"
                + "}\n",
                Collections.singletonList("class"));
    }

    public void testClassKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "\n"
                + "        class Class {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("class"));
    }

    public void testEnumKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("enum"));
    }

    public void testEnumKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "e",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("enum"));
    }

    public void testEnumKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "e",
                "enum Test {\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("enum"));
    }

    public void testEnumKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n"
                + "\n"
                + "enum Enum {\n"
                + "}\n",
                Collections.singletonList("enum"));
    }

    public void testExtendsKeywordCompletionForClass() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test |{\n"
                + "}",
                "class Test extends  {\n"
                + "}",
                Collections.singletonList("extends"));
    }

    public void testExtendsKeywordCompletionForInterface() throws IOException {
        doAbbreviationInsert(
                "e",
                "interface Test |{\n"
                + "}",
                "interface Test extends  {\n"
                + "}",
                Collections.singletonList("extends"));
    }

    public void testElseKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "    void test() {\n"
                + "        if |(true) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (true) {\n"
                + "        } else {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("else"));
    }

    public void testVoidKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    void method() {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("void"));
    }

    public void testVoidKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "v",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "    void method();\n"
                + "    \n"
                + "}",
                Collections.singletonList("void"));
    }

    public void testVoidKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "v",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "\n"
                + "    void method() {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList("void"));
    }

    public void testImportKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "i",
                "|\n"
                + "class Test {\n"
                + "}",
                "\n"
                + "class Test {\n"
                + "}",
                Arrays.asList("import", "interface"));
    }

    public void testReturnKeywordCompletionInBlockTree() throws IOException {
        doAbbreviationInsert(
                "r",
                "class Test {\n"
                + "    int size() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int size() {\n"
                + "        return 0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("return"));
    }

    public void testReturnKeywordCompletionInSwitchTree() throws IOException {
        doAbbreviationInsert(
                "r",
                "class Test {\n"
                + "    int test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "                |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "            return 0;\n"
                + "                \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("return"));
    }

    public void testThisKeywordCompletionInBlockTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "import java.util.List;\n"
                + "class Test {\n"
                + "    private double salary;\n"
                + "    private String employeeName;\n"
                + "    private int age;\n"
                + "    private List<String> departments;\n"
                + "    int test(String name, int age, double salary, StringBuilder builder) {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "import java.util.List;\n"
                + "class Test {\n"
                + "    private double salary;\n"
                + "    private String employeeName;\n"
                + "    private int age;\n"
                + "    private List<String> departments;\n"
                + "    int test(String name, int age, double salary, StringBuilder builder) {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("this", "throw", "try"));
    }

    public void testWhenThereIsNoMatchesBetweenFieldsAndParametersThenDoNotinvokeCompletionForThisKeywordInBlockTree()
            throws IOException {
        doAbbreviationInsert(
                "t",
                "import java.util.List;\n"
                + "class Test {\n"
                + "    private List<String> departments;\n"
                + "    int test(String name, int age, double salary, StringBuilder builder) {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "import java.util.List;\n"
                + "class Test {\n"
                + "    private List<String> departments;\n"
                + "    int test(String name, int age, double salary, StringBuilder builder) {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("throw", "try"));
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
        Preferences.setMethodInvocationFlag(methodInvocation);
        Preferences.setStaticMethodInvocationFlag(staticMethodInvocation);
        Preferences.setChainedMethodInvocationFlag(chainedMethodInvocation);
        Preferences.setChainedFieldAccessFlag(chainedFieldAccess);
        Preferences.setChainedEnumConstantAccessFlag(chainedEnumConstantAccess);
        Preferences.setLocalMethodInvocationFlag(localMethodInvocation);
        Preferences.setStaticFieldAccessFlag(staticFieldAccess);
        Preferences.setLocalVariableFlag(localVariable);
        Preferences.setFieldFlag(field);
        Preferences.setParameterFlag(parameter);
        Preferences.setEnumConstantFlag(enumConstant);
        Preferences.setExceptionParameterFlag(exceptionParameter);
        Preferences.setResourceVariableFlag(resourceVariable);
        Preferences.setInternalTypeFlag(internalType);
        Preferences.setExternalTypeFlag(externalType);
        Preferences.setImportedTypeFlag(importedType);
        Preferences.setSamePackageTypeFlag(samePackageType);
        Preferences.setKeywordFlag(keyword);
        Preferences.setModifierFlag(modifier);
        Preferences.setPrimitiveTypeFlag(primitiveType);
    }
}
