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
public class NameCompletionTest extends GeneralCompletionTest {

    public NameCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(NameCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setFieldFlag(true);
    }

    public void testCompletionOfFieldName() throws IOException {
        doAbbreviationInsert(
                "ll",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    private LinkedList |;\n"
                + "}",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    private LinkedList linkedList;\n"
                + "}",
                Collections.singletonList("linkedList"));
    }

    public void testCompletionOfMethodParameterName() throws IOException {
        doAbbreviationInsert(
                "ll",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    void test(LinkedList |) {\n"
                + "    }\n"
                + "}",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    void test(LinkedList linkedList) {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("linkedList"));
    }

    public void testCompletionOfLocalVariableName() throws IOException {
        doAbbreviationInsert(
                "ll",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        LinkedList |;\n"
                + "    }\n"
                + "}",
                "import java.util.LinkedList;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        LinkedList linkedList;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("linkedList"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
