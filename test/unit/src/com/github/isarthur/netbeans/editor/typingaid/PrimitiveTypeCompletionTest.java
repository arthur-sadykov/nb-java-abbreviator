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
public class PrimitiveTypeCompletionTest extends NbTestCase {

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
    private boolean primitiveType;
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

    public PrimitiveTypeCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(PrimitiveTypeCompletionTest.class)
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
        setConfigurationForPrimitiveTypeCompletion();
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
        primitiveType = Settings.getSettingForPrimitiveType();
    }

    private void setConfigurationForPrimitiveTypeCompletion() {
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
        Settings.setSettingForKeyword(false);
        Settings.setSettingForModifier(false);
        Settings.setSettingForPrimitiveType(true);
    }

    public void testBooleanByteKeywordsCompletionInBlock() throws IOException {
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
                Arrays.asList("boolean", "byte"));
    }

    public void testBooleanByteKeywordsCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        boolean bool = | object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        boolean bool =  object;\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean", "byte"));
    }

    public void testBooleanByteKeywordsCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, ) {\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean", "byte"));
    }

    public void testCharKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        char c = '\\u0000';\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("char"));
    }

    public void testCharKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        char ch = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        char ch = (char) object;\n"
                + "    }\n"
                + "}",
                Arrays.asList("char"));
    }

    public void testCharKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, char c, ) {\n"
                + "    }\n"
                + "}",
                Arrays.asList("char"));
    }

    public void testIntKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        int i = 0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("int"));
    }

    public void testIntKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        int integer = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        int integer = (int) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("int"));
    }

    public void testIntKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, int i, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("int"));
    }

    public void testLongKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "l",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        long l = 0L;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("long"));
    }

    public void testLongKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "l",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        long number = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        long number = (long) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("long"));
    }

    public void testLongKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "l",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, long l, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("long"));
    }

    public void testShortKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        short s = 0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("short"));
    }

    public void testShortKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        short number = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        short number = (short) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("short"));
    }

    public void testShortKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, short s, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("short"));
    }

    public void testFloatKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        float f = 0.0F;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("float"));
    }

    public void testFloatKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        float number = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        float number = (float) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("float"));
    }

    public void testFloatKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, float f, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("float"));
    }

    public void testDoubleKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        double d = 0.0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("double"));
    }

    public void testDoubleKeywordCompletionInTypeCast() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        double number = |object;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        double number = (double) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("double"));
    }

    public void testDoubleKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, double d, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("double"));
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
        Settings.setSettingForPrimitiveType(primitiveType);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }
}
