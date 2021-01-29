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
public class LiteralCompletionTest extends GeneralCompletionTest {

    public LiteralCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(LiteralCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setLiteralFlag(true);
    }

    public void testTrueLiteralInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        valid = |\n;"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        valid = \n"
                + "        true;    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInEqualToTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid == |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid == true) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInMethodInvocationTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, true);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInNewClassTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, true);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInNotEqualToTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid != |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid != true) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInParenthesizedTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (true) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInReturnTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    boolean test() {\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    boolean test() {\n"
                + "        return true;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInVariableTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid =true;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInTrueExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? | : false;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ?  true: false;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testTrueLiteralInFalseExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? false : |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? false : true;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("true"));
    }

    public void testFalseLiteralInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        valid = |\n;"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        valid = \n"
                + "        false;    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInEqualToTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid == |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid == false) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInMethodInvocationTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, false);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInNewClassTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, false);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInNotEqualToTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid != |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid;\n"
                + "        if (valid != false) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInParenthesizedTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (false) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInReturnTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    boolean test() {\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    boolean test() {\n"
                + "        return false;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInVariableTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid =false;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInTrueExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? | : true;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ?  false: true;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testFalseLiteralInFalseExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? true : |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        valid = another == null ? true : false;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("false"));
    }

    public void testNullLiteralInAssignmentTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        valid = |\n;"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        valid = \n"
                + "        null;    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInEqualToTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        if (valid == |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        if (valid == null) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInMethodInvocationTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        isValid(0, null);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInNewClassTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, |);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        Clazz clazz = new Clazz(0, null);\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInNotEqualToTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        if (valid != |) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid;\n"
                + "        if (valid != null) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInParenthesizedTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (null) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInReturnTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    String test() {\n"
                + "        return |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    String test() {\n"
                + "        return null;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInVariableTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid = |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        String valid =null;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInTrueExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        String name = valid ? | : another;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        String name = valid ?  null: another;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    public void testNullLiteralInFalseExpressionOfConditionalExpressionTree() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        String name = valid ? another : |;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean valid = true;\n"
                + "        String another = \"\";\n"
                + "        String name = valid ? another : null;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("null"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
