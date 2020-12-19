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
    private boolean literal;
    private boolean primitiveType;
    private boolean modifier;
    private boolean externalType;
    private boolean internalType;
    private boolean globalType;
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
        abbreviation = new JavaAbbreviation();
        storeSettings();
        setConfigurationForPrimitiveTypeCompletion();
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
        globalType = Preferences.getGlobalTypeFlag();
        keyword = Preferences.getKeywordFlag();
        literal = Preferences.getLiteralFlag();
        modifier = Preferences.getModifierFlag();
        primitiveType = Preferences.getPrimitiveTypeFlag();
    }

    private void setConfigurationForPrimitiveTypeCompletion() {
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
        Preferences.setGlobalTypeFlag(false);
        Preferences.setKeywordFlag(false);
        Preferences.setLiteralFlag(false);
        Preferences.setModifierFlag(false);
        Preferences.setPrimitiveTypeFlag(true);
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

    public void testBooleanByteKeywordsCompletionInField() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "    \n"
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

    public void testCharKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private char c;\n"
                + "    \n"
                + "}",
                Collections.singletonList("char"));
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

    public void testIntKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private int i;\n"
                + "    \n"
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

    public void testLongKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "l",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private long l;\n"
                + "    \n"
                + "}",
                Collections.singletonList("long"));
    }

    public void testShortStringKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "s",
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
                Arrays.asList("String", "short"));
    }

    public void testShortStringKeywordCompletionInTypeCast() throws IOException {
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
                + "        short number = object;\n"
                + "    }\n"
                + "}",
                Arrays.asList("String", "short"));
    }

    public void testShortStringKeywordCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test(int count, ) {\n"
                + "    }\n"
                + "}",
                Arrays.asList("String", "short"));
    }

    public void testShortStringKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "    \n"
                + "}",
                Arrays.asList("String", "short"));
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

    public void testFloatKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private float f;\n"
                + "    \n"
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

    public void testDoubleKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private double d;\n"
                + "    \n"
                + "}",
                Collections.singletonList("double"));
    }

    public void testDoNotInvokeCompletionInClassOrEnumDeclarationBeforeLeftBrace() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test | {\n"
                + "}",
                "class Test  {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "d",
                "class Test |{\n"
                + "}",
                "class Test {\n"
                + "}",
                Collections.emptyList());
    }

    public void testDoNotInvokeCompletionInMethodDeclarationOutsideOfParentheses() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test()| {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test() |{\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
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
        Preferences.setGlobalTypeFlag(globalType);
        Preferences.setKeywordFlag(keyword);
        Preferences.setLiteralFlag(literal);
        Preferences.setModifierFlag(modifier);
        Preferences.setPrimitiveTypeFlag(primitiveType);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }
}
