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
                + "        List<String> list =new ArrayList();\n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.ArrayList"));
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
                + "        ArrayList arrayList = new ArrayList();\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("java.util.ArrayList"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
