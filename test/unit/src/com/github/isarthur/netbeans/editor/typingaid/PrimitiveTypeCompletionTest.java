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
public class PrimitiveTypeCompletionTest extends GeneralCompletionTest {

    public PrimitiveTypeCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(PrimitiveTypeCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
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

    public void testBooleanByteKeywordsCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "b",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, );\n"
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
                Collections.singletonList("char"));
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
                Collections.singletonList("char"));
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
                Collections.singletonList("char"));
    }

    public void testCharKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "c",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, char c, );\n"
                + "}",
                Collections.singletonList("char"));
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
                Collections.singletonList("int"));
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

    public void testIntKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "i",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, int i, );\n"
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
                Collections.singletonList("long"));
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

    public void testLongKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "l",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, long l, );\n"
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
                Collections.singletonList("short"));
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

    public void testShortKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "s",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, short s, );\n"
                + "}",
                Collections.singletonList("short"));
    }

    public void testShortKeywordCompletionInField() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    private short s;\n"
                + "    \n"
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
                Collections.singletonList("float"));
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

    public void testFloatKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "f",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, float f, );\n"
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
                Collections.singletonList("double"));
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

    public void testDoubleKeywordCompletionInInterfaceMethodParameter() throws IOException {
        doAbbreviationInsert(
                "d",
                "interface Test {\n"
                + "    void test(int count, |);\n"
                + "}",
                "interface Test {\n"
                + "    void test(int count, double d, );\n"
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

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
