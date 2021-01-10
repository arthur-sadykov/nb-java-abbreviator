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
public class EnumConstantCompletionTest extends GeneralCompletionTest {

    public EnumConstantCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(EnumConstantCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setEnumConstantFlag(true);
    }

    public void testEnumConstantCompletionWhenCaretPositionBeforeColon() throws IOException {
        doAbbreviationInsert(
                "g",
                "class Test {\n"
                + "    Type type = Type.GLOBAL;\n"
                + "    void setExternalTypeFlag() {\n"
                + "        switch(type) {\n"
                + "            case | :\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "    enum Type {\n"
                + "        GLOBAL,\n"
                + "        LOCAL;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    Type type = Type.GLOBAL;\n"
                + "    void setExternalTypeFlag() {\n"
                + "        switch(type) {\n"
                + "            case  GLOBAL:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "    enum Type {\n"
                + "        GLOBAL,\n"
                + "        LOCAL;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("GLOBAL"));
    }

    public void testEnumConstantCompletionWhenCaretPositionEqualToColonPosition() throws IOException {
        doAbbreviationInsert(
                "l",
                "class Test {\n"
                + "    Type type = Type.GLOBAL;\n"
                + "    void setExternalTypeFlag() {\n"
                + "        switch(type) {\n"
                + "            case |:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "    enum Type {\n"
                + "        GLOBAL,\n"
                + "        LOCAL;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    Type type = Type.GLOBAL;\n"
                + "    void setExternalTypeFlag() {\n"
                + "        switch(type) {\n"
                + "            case LOCAL:\n"
                + "                break;\n"
                + "        }\n"
                + "    }\n"
                + "    enum Type {\n"
                + "        GLOBAL,\n"
                + "        LOCAL;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("LOCAL"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
