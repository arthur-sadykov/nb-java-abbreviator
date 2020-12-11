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
public class GeneralCompletionTest extends NbTestCase {

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

    public GeneralCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(GeneralCompletionTest.class)
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
        importedType = Settings.getSettingForImportedType();
        keyword = Settings.getSettingForKeyword();
        modifier = Settings.getSettingForModifier();
        primitiveType = Settings.getSettingForPrimitiveType();
    }

    public void testShouldSuggestCompletionForParameterName() throws IOException {
        doAbbreviationInsert(
                "nos",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        if (numberOfSpaces) {\n"
                + "        }\n"
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
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    private String branchName;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        if (branchName) {\n"
                + "        }\n"
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
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String applicationContext = \"\";\n"
                + "        if (applicationContext) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("applicationContext"));
    }

    public void testShouldSuggestCompletionForKeyword() throws IOException {
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
        doAbbreviationInsert(
                "s",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String applicationContext = \"\";\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String applicationContext = \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("String", "short", "switch"));
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
                + "        boolean b = branchName.isEmpty();\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
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
                + "        if (branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
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
                + "        while (branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
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
                + "        if (branchName.isBlank() && branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
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
                + "        if (branchName.isBlank() && !branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("boolean b = branchName.isEmpty();"));
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
                + "        branchName.;\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
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
                + "        System.out.println(branchName.);\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
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
                + "        boolean empty = branchName.;\n"
                + "    }\n"
                + "}",
                Arrays.asList("isEmpty()"));
    }

    public void shouldNotAddDuplicatesToTheResultingList() throws IOException {
        doAbbreviationInsert(
                "nos",
                "public class Test {\n"
                + "    private int numberOfSpaces;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String numberOfSpaces = \"\";\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    private int numberOfSpaces;\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String numberOfSpaces = \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("numberOfSpaces"));
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
        Settings.setSettingForImportedType(importedType);
        Settings.setSettingForKeyword(keyword);
        Settings.setSettingForModifier(modifier);
        Settings.setSettingForPrimitiveType(primitiveType);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }
}
