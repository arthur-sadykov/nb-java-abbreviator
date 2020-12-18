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
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
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
public class LocalElementCompletionTest extends NbTestCase {

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

    public LocalElementCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(LocalElementCompletionTest.class)
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
        setConfigurationForLocalElementCompletion();
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

    private void setConfigurationForLocalElementCompletion() {
        Preferences.setStaticMethodInvocationFlag(false);
        Preferences.setStaticFieldAccessFlag(false);
        Preferences.setMethodInvocationFlag(false);
        Preferences.setChainedMethodInvocationFlag(false);
        Preferences.setChainedFieldAccessFlag(false);
        Preferences.setChainedEnumConstantAccessFlag(false);
        Preferences.setLocalMethodInvocationFlag(false);
        Preferences.setLocalVariableFlag(true);
        Preferences.setFieldFlag(true);
        Preferences.setParameterFlag(true);
        Preferences.setEnumConstantFlag(true);
        Preferences.setExceptionParameterFlag(true);
        Preferences.setResourceVariableFlag(true);
        Preferences.setInternalTypeFlag(false);
        Preferences.setExternalTypeFlag(false);
        Preferences.setImportedTypeFlag(false);
        Preferences.setSamePackageTypeFlag(false);
        Preferences.setKeywordFlag(false);
        Preferences.setModifierFlag(false);
        Preferences.setPrimitiveTypeFlag(false);
    }

    public void testLocalElementCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "noc",
                "class Test {\n"
                + "    int numberOfClasses = 0;\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int numberOfClasses = 0;\n"
                + "    void test() {\n"
                + "        numberOfClasses = 0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("numberOfClasses"));
    }

    public void testLocalElementCompletionInCaseTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "class Test {\n"
                + "    int numberOfClasses = 0;\n"
                + "    void test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "                |\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int numberOfClasses = 0;\n"
                + "    void test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "            numberOfClasses = 0;\n"
                + "                \n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("numberOfClasses"));
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
