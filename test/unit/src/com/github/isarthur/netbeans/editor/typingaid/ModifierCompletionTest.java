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
public class ModifierCompletionTest extends NbTestCase {

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

    public ModifierCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(ModifierCompletionTest.class)
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
        setConfigurationForModifierCompletion();
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

    private void setConfigurationForModifierCompletion() {
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
        Settings.setSettingForImportedType(false);
        Settings.setSettingForKeyword(false);
        Settings.setSettingForModifier(true);
        Settings.setSettingForPrimitiveType(false);
    }

    public void testPublicModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|class Test {\n"
                + "}",
                "public class Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInZeroPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "|final strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInFirstPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "final |strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInSecondPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "final strictfp |class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testAbstractModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "|class Test {\n"
                + "}",
                "abstract class Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "|public strictfp class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "public |strictfp class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "public strictfp |class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testFinalModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "|class Test {\n"
                + "}",
                "final class Test {\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInZeroPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "|public strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInFirstPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "public |strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInSecondPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "public strictfp |class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testStrictfpModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|class Test {\n"
                + "}",
                "strictfp class Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public abstract class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |abstract class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "public abstract |class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForTopLevelClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "|public abstract class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "public |abstract class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "public abstract |class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForTopLevelClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "|public final class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "public |final class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "public final |class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
    }

    public void testAccessModifierCompletionForInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInFirstPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInSecondPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testAbstractModifierCompletionForInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    abstract class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testFinalModifierCompletionForInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInZeroPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInFirstPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInSecondPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testStrictfpModifierCompletionForInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testWhenAccessModifierIsPresentThenDoNotSuggestAccessModifierCompletionForInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    private |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    private final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    protected final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    protected final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |public abstract class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    public |abstract class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    public abstract |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testAbstractModifierCompletionForMethodLocalInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testFinalModifierCompletionForMethodLocalInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInZeroPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInFirstPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForMethodLocalInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForMethodLocalInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testPublicModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|interface Test {\n"
                + "}",
                "public interface Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInZeroPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "|abstract strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInFirstPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "abstract |strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInSecondPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "abstract strictfp |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testAbstractModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "|interface Test {\n"
                + "}",
                "abstract interface Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "|public strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "public |strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "public strictfp |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testStrictfpModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|interface Test {\n"
                + "}",
                "strictfp interface Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public abstract interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |abstract interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "public abstract |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testAccessModifierCompletionForInnerInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |abstract interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static abstract |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAbstractModifierCompletionForInnerInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public static |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testStaticModifierCompletionForInnerInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInFirstPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInSecondPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStrictfpModifierCompletionForInnerInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForInnerInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testPublicModifierCompletionForTopLevelEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|enum Test {\n"
                + "}",
                "public enum Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInZeroPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "|strictfp enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testPublicModifierCompletionInFirstPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "strictfp |enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Arrays.asList("public"));
    }

    public void testStrictfpModifierCompletionForTopLevelEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|enum Test {\n"
                + "}",
                "strictfp enum Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testAccessModifierCompletionForInnerEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |abstract enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static abstract |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForInnerEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInFirstPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInSecondPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStrictfpModifierCompletionForInnerEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForInnerEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("strictfp"));
    }

    public void testAccessModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |static final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAbstractModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    abstract void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    |protected void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    protected abstract void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    protected |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    protected abstract void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("abstract"));
    }

    public void testStaticModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testFinalModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    public |static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    public static |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testNativeModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    native void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("native"));
    }

    public void testNativeModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("native"));
    }

    public void testNativeModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    public |static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("native"));
    }

    public void testNativeModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    public static |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("native"));
    }

    public void testSynchronizedModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionForMethodWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInZeroPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInFirstPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInSecondPositionForMethod() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testAccessModifierCompletionForFieldWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroPositionForField() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |static final int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstPositionForField() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static |final int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondPositionForField() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static final |int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForFieldWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    static int count;\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInZeroPositionForField() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |private transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInFirstPositionForField() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    private |transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticModifierCompletionInSecondPositionForField() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    private transient |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("static"));
    }

    public void testFinalModifierCompletionForFieldWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    final int count;\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInZeroPositionForField() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |private transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInFirstPositionForField() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private |transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testFinalModifierCompletionInSecondPositionForField() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private transient |int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Arrays.asList("final"));
    }

    public void testTransientModifierCompletionForFieldWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    transient int count;\n"
                + "}",
                Arrays.asList("transient"));
    }

    public void testTransientModifierCompletionInZeroPositionForField() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    |private static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("transient"));
    }

    public void testTransientModifierCompletionInFirstPositionForField() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    private |static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("transient"));
    }

    public void testTransientModifierCompletionInSecondPositionForField() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    private static |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Arrays.asList("transient"));
    }

    public void testVolatileModifierCompletionForFieldWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    volatile int count;\n"
                + "}",
                Arrays.asList("volatile"));
    }

    public void testVolatileModifierCompletionInZeroPositionForField() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |private static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Arrays.asList("volatile"));
    }

    public void testVolatileModifierCompletionInFirstPositionForField() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private |static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Arrays.asList("volatile"));
    }

    public void testVolatileModifierCompletionInSecondPositionForField() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private static |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Arrays.asList("volatile"));
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestVolatileModifierCompletionForMethod()
            throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |private final int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private |final int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private final |int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenVolatileModifierIsPresentThenDoNotSuggestFinalModifierCompletionForMethod()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |private volatile int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private |volatile int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private volatile |int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
    }

    public void testFinalModifierCompletionForLocalVariable() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private void get() {\n"
                + "        |int count = 0;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    private void get() {\n"
                + "        final int count = 0;\n"
                + "    }\n"
                + "}",
                Arrays.asList("final"));
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
