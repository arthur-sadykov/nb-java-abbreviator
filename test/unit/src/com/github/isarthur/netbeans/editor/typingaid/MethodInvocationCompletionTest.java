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
import java.util.Collections;
import junit.framework.Test;

/**
 *
 * @author: Arthur Sadykov
 */
public class MethodInvocationCompletionTest extends GeneralCompletionTest {

    public MethodInvocationCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(MethodInvocationCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setMethodInvocationFlag(true);
        Preferences.setLocalVariableFlag(true);
        Preferences.setFieldFlag(true);
        Preferences.setParameterFlag(true);
        Preferences.setExceptionParameterFlag(true);
        Preferences.setResourceVariableFlag(true);
    }

    public void testMethodInvocationCompletionInAndAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces &= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces &= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInAndTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces & |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces & branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int length = 10;\n"
                + "        length = |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int length = 10;\n"
                + "        length = branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInBitwiseComplementTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces & ~|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces & ~branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInBlockTree() throws IOException {
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
                Collections.singletonList("boolean b = branchName.isEmpty();"));
    }

    public void testMethodInvocationCompletionInCaseTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        switch (numberOfSpaces) {\n"
                + "            case 0:\n"
                + "                |\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        switch (numberOfSpaces) {\n"
                + "            case 0:\n"
                + "            int i = branchName.length();\n"
                + "                \n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInConditionalAndTree() throws IOException {
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
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInTrueExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean valid = true ? | : false;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean valid = true ?  branchName.isEmpty(): false;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInFalseExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean valid = true ? false : |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean valid = true ? false : branchName.isEmpty();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInDivideAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces /= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces /= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInDivideTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        double count = numberOfSpaces / |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        double count = numberOfSpaces / branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInEqualToTree() throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && true == |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && true == branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInGreaterThanTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces > |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces > branchName.length()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInGreaterThanEqualTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces >= |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces >= branchName.length()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInLeftShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces <<= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces <<= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInLeftShiftTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces << |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces << branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInLessThanTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces < |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces < branchName.length()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInLessThanEqualTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces <= |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && numberOfSpaces <= branchName.length()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInMethodInvocationTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test1() {\n"
                + "        String branchName = \"\";\n"
                + "        test2(branchName, |);\n"
                + "    }\n"
                + "    public void test2(String name, int length) {\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test1() {\n"
                + "        String branchName = \"\";\n"
                + "        test2(branchName, branchName.length());\n"
                + "    }\n"
                + "    public void test2(String name, int length) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInMinusAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces -= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces -= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInMinusTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces - |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces - branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInMultiplyAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces *= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces *= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInMultiplyTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces * |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces * branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInLogicalComplementTree() throws IOException {
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
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInNewClassTree() throws IOException {
        doAbbreviationInsert(
                "bn.i",
                "import java.io.File;\n"
                + "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        File file = new File<>(branchName, |);\n"
                + "    }\n"
                + "}",
                "import java.io.File;\n"
                + "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        File file = new File<>(branchName, branchName.intern());\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.intern()"));
    }

    public void testMethodInvocationCompletionInNotEqualToTree() throws IOException {
        doAbbreviationInsert(
                "bn.ie",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && true != |) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && true != branchName.isEmpty()) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInParenthesizedTree() throws IOException {
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
                Collections.singletonList("branchName.isEmpty()"));
    }

    public void testMethodInvocationCompletionInPlusAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces += |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces += branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInPlusTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces + |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces + branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInPrefixDecrementTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        --|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        --branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInPrefixIncrementTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        ++|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        ++branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInRemainderAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces %= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces %= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInRemainderTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces % |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces % branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInReturnTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public int test() {\n"
                + "        String branchName = \"\";\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public int test() {\n"
                + "        String branchName = \"\";\n"
                + "        return branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInRightShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces >>= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces >>= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInRightShiftTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces >> |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces >> branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInUnsignedRightShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces >>>= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces >>>= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInUnsignedRightShiftTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces >>> |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces >>> branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInVariableTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces =branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInXorAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces ^= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        numberOfSpaces ^= branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    public void testMethodInvocationCompletionInXorTree() throws IOException {
        doAbbreviationInsert(
                "bn.l",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces ^ |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = 10;\n"
                + "        int count = numberOfSpaces ^ branchName.length();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName.length()"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
