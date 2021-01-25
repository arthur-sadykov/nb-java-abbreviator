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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.Test;

/**
 *
 * @author: Arthur Sadykov
 */
public class GlobalTypeCompletionTest extends GeneralCompletionTest {

    public GlobalTypeCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(GlobalTypeCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setGlobalTypeFlag(true);
        Preferences.setModifierFlag(true);
    }

    public void testImportedTypeCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "iot",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        InstanceOfTree instanceOfTree = null;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("com.sun.source.tree.InstanceOfTree"));
    }

    public void testImportedTypeCompletionInVariableTypeCast() throws IOException {
        doAbbreviationInsert(
                "iot",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        InstanceOfTree instance = |object;\n"
                + "    }\n"
                + "}",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        InstanceOfTree instance = (InstanceOfTree) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("com.sun.source.tree.InstanceOfTree"));
    }

    public void testImportedTypeCompletionInVariable() throws IOException {
        doAbbreviationInsert(
                "al",
                "package test;\n"
                + "import com.sun.source.tree.ClassTree;\n"
                + "import java.util.ArrayList;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        List<String> list = | ;\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "import com.sun.source.tree.ClassTree;\n"
                + "import java.util.ArrayList;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        Object object = null;\n"
                + "        List<String> list =new ArrayList<>();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.ArrayList<String>"));
    }

    public void testImportedTypeCompletionInMethodParameter() throws IOException {
        doAbbreviationInsert(
                "iot",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test(int count, |) {\n"
                + "    }\n"
                + "}",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    void test(int count, InstanceOfTree instanceOfTree, ) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("com.sun.source.tree.InstanceOfTree"));
    }

    public void testImportedTypeCompletionInField() throws IOException {
        doAbbreviationInsert(
                "iot",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    |\n"
                + "}",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "\n"
                + "    private InstanceOfTree instanceOfTree;\n"
                + "    \n"
                + "}",
                Collections.singletonList("com.sun.source.tree.InstanceOfTree"));
    }

    public void testImportedTypeCompletionInReturnStatement() throws IOException {
        doAbbreviationInsert(
                "fd",
                "package test;\n"
                + "import java.awt.FileDialog;\n"
                + "class InstanceOfTree {\n"
                + "    FileDialog test() {\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "import java.awt.FileDialog;\n"
                + "class InstanceOfTree {\n"
                + "    FileDialog test() {\n"
                + "        return new FileDialog(null);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.awt.FileDialog"));
    }

    public void testImportedTypeCompletionInParameterizedType() throws IOException {
        doAbbreviationInsert(
                "f",
                "package test;\n"
                + "import java.util.List;\n"
                + "class InstanceOfTree {\n"
                + "    private List<|> parts;\n"
                + "}",
                "package test;\n"
                + "import java.util.List;\n"
                + "class InstanceOfTree {\n"
                + "    private List<Float> parts;\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testImportedTypeCompletionInParameterizedTypeInZeroPosition() throws IOException {
        doAbbreviationInsert(
                "f",
                "package test;\n"
                + "import java.util.Map;\n"
                + "class InstanceOfTree {\n"
                + "    private Map<| String> parts;\n"
                + "}",
                "package test;\n"
                + "import java.util.Map;\n"
                + "class InstanceOfTree {\n"
                + "    private Map< Float, String> parts;\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testImportedTypeCompletionInParameterizedTypeInFirstPosition() throws IOException {
        doAbbreviationInsert(
                "f",
                "package test;\n"
                + "import java.util.Map;\n"
                + "class InstanceOfTree {\n"
                + "    private Map<String |> parts;\n"
                + "}",
                "package test;\n"
                + "import java.util.Map;\n"
                + "class InstanceOfTree {\n"
                + "    private Map<String, Float > parts;\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testImportedTypeCompletionInParameterizedTypeInSecondPosition() throws IOException {
        doAbbreviationInsert(
                "f",
                "package test;\n"
                + "import java.util.function.BiFunction;\n"
                + "class InstanceOfTree {\n"
                + "    private BiFunction<String, Integer |> function;\n"
                + "}",
                "package test;\n"
                + "import java.util.function.BiFunction;\n"
                + "class InstanceOfTree {\n"
                + "    private BiFunction<String, Integer, Float > function;\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testWhenTypeHasConstructorThenNewClassMustBeSubstitutedAsVariableInitializerInBlock() throws IOException {
        doAbbreviationInsert(
                "al",
                "import java.util.ArrayList;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "import java.util.ArrayList;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        ArrayList<String> arrayList = new ArrayList<>();\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.ArrayList<String>"));
    }

    public void testGenericTypeCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "am",
                "import java.util.AbstractMap;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "import java.util.AbstractMap;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        AbstractMap<String, String> abstractMap = null;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.AbstractMap<String, String>"));
    }

    public void testGlobalTypeCompletionInCatchTree() throws IOException {
        doAbbreviationInsert(
                "re",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (|ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (RuntimeException ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.lang.RuntimeException"));
        doAbbreviationInsert(
                "re",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch| (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "re",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch |(ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "re",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex)| {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testImportedTypeCompletionInReturnTypeCast() throws IOException {
        doAbbreviationInsert(
                "iot",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    InstanceOfTree test() {\n"
                + "        Object object = null;\n"
                + "        return |object;\n"
                + "    }\n"
                + "}",
                "import com.sun.source.tree.ClassTree;\n"
                + "import com.sun.source.tree.InstanceOfTree;\n"
                + "java.io.*;\n"
                + "class Test {\n"
                + "    InstanceOfTree test() {\n"
                + "        Object object = null;\n"
                + "        return (InstanceOfTree) object;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("com.sun.source.tree.InstanceOfTree"));
    }

    public void testGlobalTypeCompletionInClassDeclarationWhenNextTokenIsWhitespaceAndMultipleResultsAreExpected()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    \n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("java.lang.Package", "java.lang.Process"));
    }

    public void testGlobalTypeCompletionInClassDeclarationWhenNextTokenIsNotWhitespaceAndMultipleResultsAreExpected()
            throws IOException {
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

    public void testGlobalTypeCompletionInClassDeclarationWhenNextTokenIsWhitespaceAndOneResultIsExpected()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private Float float1;\n"
                + "    \n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testGlobalTypeCompletionInClassDeclarationWhenNextTokenIsNotWhitespaceAndOneResultIsExpected()
            throws IOException {
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
                Collections.singletonList("final"));
    }

    public void testGlobalTypeCompletionInEnumDeclarationWhenNextTokenIsWhitespaceAndMultipleResultsAreExpected()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    \n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("java.lang.Package", "java.lang.Process"));
    }

    public void testGlobalTypeCompletionInEnumDeclarationWhenNextTokenIsNotWhitespaceAndMultipleResultsAreExpected()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testGlobalTypeCompletionInEnumDeclarationWhenNextTokenIsWhitespaceAndOneResultIsExpected()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    private Float float1;\n"
                + "    \n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.lang.Float"));
    }

    public void testGlobalTypeCompletionInEnumDeclarationWhenNextTokenIsNotWhitespaceAndOneResultIsExpected()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testGlobalTypeCompletionInInterfaceDeclarationWhenNextTokenIsWhitespaceAndMultipleResultsAreExpected()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "interface Test {\n"
                + "    |\n"
                + "    void test();\n"
                + "}",
                "interface Test {\n"
                + "    \n"
                + "    void test();\n"
                + "}",
                Arrays.asList("java.lang.Package", "java.lang.Process"));
    }

    public void testGlobalTypeCompletionInInterfaceDeclarationWhenNextTokenIsNotWhitespaceAndOneResultIsExpected()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "interface Test {\n"
                + "    |void test();\n"
                + "}",
                "interface Test {\n"
                + "    public void test();\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testGlobalTypeCompletionOnLeftSideOfAssignmentOperatorInBlock() throws IOException {
        doAbbreviationInsert(
                "al",
                "import java.util.ArrayList;\n"
                + "class Test {\n"
                + "    public void test() {\n"
                + "        | = new ArrayList();\n"
                + "    }\n"
                + "}",
                "import java.util.ArrayList;\n"
                + "class Test {\n"
                + "    public void test() {\n"
                + "        ArrayList<String> arrayList = new ArrayList();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.ArrayList<String>"));
    }

    public void testGlobalTypeCompletionInInstanceofTree() throws IOException {
        doAbbreviationInsert(
                "ame",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if (object instanceof |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if (object instanceof AbstractMethodError) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("java.lang.AbstractMethodError"));
    }

    public void testGlobalTypeCompletionInExtendsClauseOfClassDeclaration() throws IOException {
        doAbbreviationInsert(
                "ame",
                "package test;\n"
                + "class Test extends |{\n"
                + "}",
                "package test;\n"
                + "class Test extends AbstractMethodError{\n"
                + "}",
                Arrays.asList("java.lang.AbstractMethodError"));
    }

    public void testGlobalTypeCompletionInExtendsClauseOfInterfaceDeclaration() throws IOException {
        doAbbreviationInsert(
                "ac",
                "package test;\n"
                + "interface Test extends |{\n"
                + "}",
                "package test;\n"
                + "interface Test extends AutoCloseable{\n"
                + "}",
                Arrays.asList("java.lang.AutoCloseable"));
    }

    public void testGlobalTypeCompletionInImplementsClauseOfClassDeclaration() throws IOException {
        doAbbreviationInsert(
                "ac",
                "package test;\n"
                + "class Test implements |{\n"
                + "}",
                "package test;\n"
                + "class Test implements AutoCloseable{\n"
                + "}",
                Arrays.asList("java.lang.AutoCloseable"));
        doAbbreviationInsert(
                "cs",
                "package test;\n"
                + "class Test implements AutoCloseable |{\n"
                + "}",
                "package test;\n"
                + "class Test implements AutoCloseable, CharSequence {\n"
                + "}",
                Arrays.asList("java.lang.CharSequence"));
    }

    public void testGlobalTypeCompletionInImplementsClauseOfEnumDeclaration() throws IOException {
        doAbbreviationInsert(
                "ac",
                "package test;\n"
                + "enum Test implements |{\n"
                + "}",
                "package test;\n"
                + "enum Test implements AutoCloseable{\n"
                + "}",
                Arrays.asList("java.lang.AutoCloseable"));
        doAbbreviationInsert(
                "cs",
                "package test;\n"
                + "enum Test implements AutoCloseable |{\n"
                + "}",
                "package test;\n"
                + "enum Test implements AutoCloseable, CharSequence {\n"
                + "}",
                Arrays.asList("java.lang.CharSequence"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
