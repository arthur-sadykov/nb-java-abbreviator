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
public class ChainedMethodInvocationCompletionTest extends GeneralCompletionTest {

    public ChainedMethodInvocationCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(ChainedMethodInvocationCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setChainedMethodInvocationFlag(true);
    }

    public void testChainedMethodInvocationCompletionThatIsPartOfExpressionStatement() throws IOException {
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
                + "        branchName.isEmpty();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("isEmpty()"));
    }

    public void testChainedMethodInvocationCompletionThatIsPartOfIfCondition() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.intern()) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("intern()"));
    }

    public void testChainedMethodInvocationCompletionThatIsPartOfMethodArgument() throws IOException {
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
                + "        System.out.println(branchName.isEmpty());\n"
                + "    }\n"
                + "}",
                Collections.singletonList("isEmpty()"));
    }

    public void testChainedMethodInvocationCompletionThatIsPartOfVariableInitializer() throws
            IOException {
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
                + "        boolean empty = branchName.isEmpty();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("isEmpty()"));
    }

    public void testMultipleChainedMethodInvocationsThatArePartOfVariableInitializer() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean empty = branchName.intern().|;\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        boolean empty = branchName.intern().intern();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("intern()"));
    }

    public void testMultipleChainedMethodInvocationsThatArePartOfIfCondition() throws IOException {
        doAbbreviationInsert(
                "i",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.intern().|) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "public class Test {\n"
                + "    public void test(int numberOfSpaces) {\n"
                + "        String branchName = \"\";\n"
                + "        if (branchName.intern().intern()) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("intern()"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
