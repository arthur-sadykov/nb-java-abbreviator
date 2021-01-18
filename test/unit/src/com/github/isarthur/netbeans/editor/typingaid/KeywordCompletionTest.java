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
public class KeywordCompletionTest extends GeneralCompletionTest {

    public KeywordCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(KeywordCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setKeywordFlag(true);
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

//    public void testImplementsKeywordCompletionForClass() throws IOException {
//        doAbbreviationInsert(
//                "i",
//                "class Test |{\n"
//                + "}",
//                "class Test implements  {\n"
//                + "}",
//                Collections.singletonList("implements"));
//    }
//    public void testImplementsKeywordCompletionForEnum() throws IOException {
//        doAbbreviationInsert(
//                "i",
//                "enum Test |{\n"
//                + "}",
//                "enum Test implements  {\n"
//                + "}",
//                Collections.singletonList("implements"));
//    }
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

//    public void testExtendsKeywordCompletionForClass() throws IOException {
//        doAbbreviationInsert(
//                "e",
//                "class Test |{\n"
//                + "}",
//                "class Test extends  {\n"
//                + "}",
//                Collections.singletonList("extends"));
//    }
//    public void testExtendsKeywordCompletionForInterface() throws IOException {
//        doAbbreviationInsert(
//                "e",
//                "interface Test |{\n"
//                + "}",
//                "interface Test extends  {\n"
//                + "}",
//                Collections.singletonList("extends"));
//    }
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

    public void testCaseKeywordCompletionInSwitchStatementWithoutCases() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case :\n"
                + "                break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("case"));
    }

    public void testCaseKeywordCompletionInZeroPositionOfSwitchStatement() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            |\n"
                + "            case 0:\n"
                + "                break;\n"
                + "            case 1:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case :\n"
                + "                break;\n"
                + "            \n"
                + "            case 0:\n"
                + "                break;\n"
                + "            case 1:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("case"));
    }

    public void testCaseKeywordCompletionInMiddlePositionOfSwitchStatement() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case 0:\n"
                + "                break;\n"
                + "            |\n"
                + "            case 1:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case 0:\n"
                + "                break;\n"
                + "                case :\n"
                + "                    break;\n"
                + "            \n"
                + "            case 1:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("case"));
    }

    public void testCaseKeywordCompletionInLastPositionOfSwitchStatement() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case 0:\n"
                + "                break;\n"
                + "            case 1:\n"
                + "                break;\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        int size = 10;\n"
                + "        switch (size) {\n"
                + "            case 0:\n"
                + "                break;\n"
                + "            case 1:\n"
                + "                break;\n"
                + "                case :\n"
                + "                    break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("case"));
    }

    public void testNewKeywordCompletionInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "package test;\n"
                + "import java.io.File;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        File file;\n"
                + "        file = |;\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "import java.io.File;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        File file;\n"
                + "        file = new File(null);\n"
                + "    }\n"
                + "}",
                Arrays.asList("new"));
    }

    public void testInstanceofKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "i",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if (object |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if (object instanceof  ) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("instanceof"));
    }

    public void testShouldOnlyInvokeCompletionForInstanceofKeywordIfIdentifierIsInFront() throws IOException {
        doAbbreviationInsert(
                "i",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    Object object = null;\n"
                + "    void test() {\n"
                + "        if () {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testSwitchAndSynchronizedKeywordCompletion() throws IOException {
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
                Arrays.asList("switch", "synchronized"));
    }

    public void testStaticKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "    static {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Arrays.asList("static"));
    }

    public void testStaticKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    static {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Arrays.asList("static"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
