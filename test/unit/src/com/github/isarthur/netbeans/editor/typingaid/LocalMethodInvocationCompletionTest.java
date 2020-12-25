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
public class LocalMethodInvocationCompletionTest extends GeneralCompletionTest {

    public LocalMethodInvocationCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(LocalMethodInvocationCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setLocalMethodInvocationFlag(true);
    }

    public void testLocalMethodInvocationCompletion() throws IOException {
        doAbbreviationInsert(
                "setf",
                "class Test {\n"
                + "    void setExternalTypeFlag() {\n"
                + "    }\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void setExternalTypeFlag() {\n"
                + "    }\n"
                + "    void test() {\n"
                + "        setExternalTypeFlag();\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("setExternalTypeFlag();"));
    }

    public void testStaticLocalMethodInvocationCompletion() throws IOException {
        doAbbreviationInsert(
                "setf",
                "class Test {\n"
                + "    static void setExternalTypeFlag() {\n"
                + "    }\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static void setExternalTypeFlag() {\n"
                + "    }\n"
                + "    void test() {\n"
                + "        setExternalTypeFlag();\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("setExternalTypeFlag();"));
    }

    public void testSuperMethodInvocationCompletion() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        boolean b = equals(null);\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("boolean b = equals(null);"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
