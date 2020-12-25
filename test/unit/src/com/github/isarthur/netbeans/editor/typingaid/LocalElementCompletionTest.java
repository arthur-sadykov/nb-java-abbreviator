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
public class LocalElementCompletionTest extends GeneralCompletionTest {

    public LocalElementCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(LocalElementCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setLocalVariableFlag(true);
        Preferences.setFieldFlag(true);
        Preferences.setParameterFlag(true);
        Preferences.setEnumConstantFlag(true);
        Preferences.setExceptionParameterFlag(true);
        Preferences.setResourceVariableFlag(true);
    }

    public void testLocalElementCompletionInAndAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count &= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count &= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInAndTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count & |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count & numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count = |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count = numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInBitwiseComplementTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count & ~|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count & ~numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInBlockTree() throws IOException {
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
                Collections.singletonList("numberOfClasses"));
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
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInConditionalAndTree() throws IOException {
        doAbbreviationInsert(
                "v",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true && |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true && valid) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("valid"));
    }

    public void testLocalElementCompletionInDivideAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count /= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count /= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInDivideTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count / |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count / numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInEqualToTree() throws IOException {
        doAbbreviationInsert(
                "v",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true == |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true == valid) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("valid"));
    }

    public void testLocalElementCompletionInGreaterThanTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 > |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 > index) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalElementCompletionInGreaterThanEqualTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 >= |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 >= index) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalElementCompletionInLeftShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count <<= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count <<= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInLeftShiftTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count << |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count << numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInLessThanTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 < |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 < index) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalElementCompletionInLessThanEqualTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 <= |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        if (10 <= index) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalElementCompletionInMethodInvocationTree() throws IOException {
        doAbbreviationInsert(
                "nol",
                "public class Test {\n"
                + "    public void test1(int numberOfLines) {\n"
                + "        String branchName = \"\";\n"
                + "        test2(branchName, |);\n"
                + "    }\n"
                + "    public void test2(String name, int length) {\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test1(int numberOfLines) {\n"
                + "        String branchName = \"\";\n"
                + "        test2(branchName, numberOfLines, );\n"
                + "    }\n"
                + "    public void test2(String name, int length) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfLines"));
    }

    public void testLocalElementCompletionInMinusAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count -= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count -= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInMinusTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count - |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count - numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInMultiplyAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count *= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count *= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInMultiplyTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count * |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count * numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInLogicalComplementTree() throws IOException {
        doAbbreviationInsert(
                "v",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && !|) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.isBlank() && !valid) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("valid"));
    }

    public void testLocalElementCompletionInNewClassTree() throws IOException {
        doAbbreviationInsert(
                "bn",
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
                + "        File file = new File<>(branchName, branchName, );\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName"));
    }

    public void testLocalElementCompletionInNotEqualToTree() throws IOException {
        doAbbreviationInsert(
                "v",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true != |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        if (true != valid) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("valid"));
    }

    public void testLocalElementCompletionInParenthesizedTree() throws IOException {
        doAbbreviationInsert(
                "v",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        String branchName = \"\";\n"
                + "        if (|) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(boolean valid) {\n"
                + "        String branchName = \"\";\n"
                + "        if (valid) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("valid"));
    }

    public void testLocalElementCompletionInPlusAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count += |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count += numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInPlusTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count + |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count + numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInPrefixDecrementTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        --|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        --index;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalCompletionInPrefixIncrementTree() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        ++|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    int index = 0;\n"
                + "    public void test() {\n"
                + "        String branchName = \"\";\n"
                + "        ++index;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("index"));
    }

    public void testLocalElementCompletionInRemainderAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count %= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count %= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInRemainderTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count % |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count % numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInReturnTree() throws IOException {
        doAbbreviationInsert(
                "bn",
                "public class Test {\n"
                + "    public String test() {\n"
                + "        String branchName = \"\";\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public String test() {\n"
                + "        String branchName = \"\";\n"
                + "        return branchName;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("branchName"));
    }

    public void testLocalElementCompletionInRightShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count >>= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count >>= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInRightShiftTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count >> |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count >> numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInSwitchTree() throws IOException {
        doAbbreviationInsert(
                "nos",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        switch (numberOfSpaces) {\n"
                + "            case 0:\n"
                + "                |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        switch (numberOfSpaces) {\n"
                + "            case 0:\n"
                + "            numberOfSpaces = 0;\n"
                + "                \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfSpaces"));
    }

    public void testLocalElementCompletionInUnsignedRightShiftAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count >>>= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count >>>= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInUnsignedRightShiftTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count >>> |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count >>> numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInVariableTree() throws IOException {
        doAbbreviationInsert(
                "nol",
                "public class Test {\n"
                + "    public void test(int numberOfLines) {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces = |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfLines) {\n"
                + "        String branchName = \"\";\n"
                + "        int numberOfSpaces =numberOfLines;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfLines"));
    }

    public void testLocalElementCompletionInXorAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count ^= |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        count ^= numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
    }

    public void testLocalElementCompletionInXorTree() throws IOException {
        doAbbreviationInsert(
                "noc",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count ^ |;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfClasses) {\n"
                + "        int count = 0;\n"
                + "        int N = count ^ numberOfClasses;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("numberOfClasses"));
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
                Collections.singletonList("numberOfSpaces"));
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
                Collections.singletonList("branchName"));
    }

    public void testShouldSuggestCompletionForLocalVariableName() throws IOException {
        doAbbreviationInsert(
                "gac",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String generalApplicationContext = \"\";\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String generalApplicationContext = \"\";\n"
                + "        if (generalApplicationContext) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("generalApplicationContext"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
