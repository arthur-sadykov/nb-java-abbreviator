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
public class InternalTypeCompletionTest extends GeneralCompletionTest {

    public InternalTypeCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(InternalTypeCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setInternalTypeFlag(true);
    }

    public void testInternalTypeCompletionInCatchTree() throws IOException {
        doAbbreviationInsert(
                "ne",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (|ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (NewException ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("test.Test.NewException"));
        doAbbreviationInsert(
                "ne",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch| (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "ne",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch |(ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "ne",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex)| {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                "package test;\n"
                + "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (ex) {\n"
                + "        }\n"
                + "    }\n"
                + "    class NewException extends Exception {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
