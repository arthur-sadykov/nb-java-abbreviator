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
public class ModifierCompletionTest extends GeneralCompletionTest {

    public ModifierCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return suite(ModifierCompletionTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        before();
    }

    @Override
    protected void setCodeCompletionConfiguration() {
        Preferences.setModifierFlag(true);
    }

    public void testPublicModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|class Test {\n"
                + "}",
                "public class Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInZeroModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "|final strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInFirstModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "final |strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInSecondModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "final strictfp |class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testAbstractModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "|class Test {\n"
                + "}",
                "abstract class Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "|public strictfp class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "public |strictfp class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "public strictfp |class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testFinalModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "|class Test {\n"
                + "}",
                "final class Test {\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "|public strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInFirstModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "public |strictfp class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInSecondModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "public strictfp |class Test {\n"
                + "}",
                "public final strictfp class Test {\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testStrictfpModifierCompletionForTopLevelClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|class Test {\n"
                + "}",
                "strictfp class Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public abstract class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |abstract class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForTopLevelClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "public abstract |class Test {\n"
                + "}",
                "public abstract strictfp class Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForTopLevelClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "|public abstract class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "public |abstract class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "public abstract |class Test {\n"
                + "}",
                "public abstract class Test {\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForTopLevelClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "|public final class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "public |final class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "public final |class Test {\n"
                + "}",
                "public final class Test {\n"
                + "}",
                Collections.emptyList());
    }

    public void testAccessModifierCompletionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static final class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInFirstModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInSecondModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testAbstractModifierCompletionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testFinalModifierCompletionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInFirstModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInSecondModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testStrictfpModifierCompletionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForClassDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testWhenAccessModifierIsPresentThenDoNotSuggestAccessModifierCompletionForClassDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    private |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    private final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    protected final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    protected final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForClassDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    |public abstract class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public |abstract class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    public abstract |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForClassDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |final class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public final |class Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testAbstractModifierCompletionForMethodLocalInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testFinalModifierCompletionForMethodLocalInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInFirstModifierPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testStrictfpModifierCompletionForMethodLocalInnerClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForMethodLocalInnerClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |final class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testWhenAbstractModifierIsPresentThenDoNotSuggestFinalModifierCompletionForMethodLocalInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        abstract strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestAbstractModifierCompletionForMethodLocalInnerClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        |final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final |strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp |class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    void test() {\n"
                + "        final strictfp class Inner {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.emptyList());
    }

    public void testPublicModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|interface Test {\n"
                + "}",
                "public interface Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInZeroModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "|abstract strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInFirstModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "abstract |strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInSecondModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "abstract strictfp |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testAbstractModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "a",
                "|interface Test {\n"
                + "}",
                "abstract interface Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "|public strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "public |strictfp interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "public strictfp |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testStrictfpModifierCompletionForTopLevelInterfaceWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|interface Test {\n"
                + "}",
                "strictfp interface Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public abstract interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |abstract interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForTopLevelInterface() throws IOException {
        doAbbreviationInsert(
                "s",
                "public abstract |interface Test {\n"
                + "}",
                "public abstract strictfp interface Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testAccessModifierCompletionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |abstract interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static abstract |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAbstractModifierCompletionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    |public static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForInterfaceDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public |static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInSecondModifierPositionForInterfaceDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "a",
                "class Outer {\n"
                + "    public static |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testStaticModifierCompletionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInFirstModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInSecondModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStrictfpModifierCompletionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForInterfaceDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForInterfaceDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |interface Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testPublicModifierCompletionForTopLevelEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "|enum Test {\n"
                + "}",
                "public enum Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInZeroModifierPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "|strictfp enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInFirstModifierPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "strictfp |enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testStrictfpModifierCompletionForTopLevelEnumWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "s",
                "|enum Test {\n"
                + "}",
                "strictfp enum Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "|public enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForTopLevelEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "public |enum Test {\n"
                + "}",
                "public strictfp enum Test {\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testAccessModifierCompletionForEnumDeclaredInsideClassWithoutModifiers() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    |static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static |abstract enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Outer {\n"
                + "    static abstract |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    static abstract enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInFirstModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInSecondModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public strictfp |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStrictfpModifierCompletionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    |public static enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public |static enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForEnumDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Outer {\n"
                + "    public static |enum Inner {\n"
                + "    }\n"
                + "}",
                "class Outer {\n"
                + "    public static strictfp enum Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("strictfp"));
    }

    public void testAccessModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |static final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAbstractModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    abstract void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    |protected void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    protected abstract void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    protected |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    protected abstract void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testStaticModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStaticModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testFinalModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    public |static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    public static |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testNativeModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testNativeModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testNativeModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    public |static void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testNativeModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "n",
                "class Test {\n"
                + "    public static |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testSynchronizedModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInFirstModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public |final void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInSecondModifierPositionForMethodDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    public final |void test() {\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testAccessModifierCompletionForFieldForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    |static final int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInFirstModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static |final int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInSecondModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "p",
                "class Test {\n"
                + "    static final |int count;\n"
                + "}",
                "class Test {\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    static int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    |private transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInFirstModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    private |transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInSecondModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "s",
                "class Test {\n"
                + "    private transient |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testFinalModifierCompletionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    final int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |private transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInFirstModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private |transient int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInSecondModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private transient |int count;\n"
                + "}",
                "class Test {\n"
                + "    private final transient int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testTransientModifierCompletionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testTransientModifierCompletionInZeroModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    |private static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testTransientModifierCompletionInFirstModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    private |static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testTransientModifierCompletionInSecondModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    private static |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testVolatileModifierCompletionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |int count;\n"
                + "}",
                "class Test {\n"
                + "    volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testVolatileModifierCompletionInZeroModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |private static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testVolatileModifierCompletionInFirstModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private |static int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testVolatileModifierCompletionInSecondModifierPositionForFieldDeclaredInsideClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private static |int count;\n"
                + "}",
                "class Test {\n"
                + "    private static volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testWhenFinalModifierIsPresentThenDoNotSuggestVolatileModifierCompletionForMethod()
            throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |private final int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private |final int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    private final |int count;\n"
                + "}",
                "class Test {\n"
                + "    private final int count;\n"
                + "}",
                Collections.emptyList());
    }

    public void testWhenVolatileModifierIsPresentThenDoNotSuggestFinalModifierCompletionForMethod()
            throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    |private volatile int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private |volatile int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private volatile |int count;\n"
                + "}",
                "class Test {\n"
                + "    private volatile int count;\n"
                + "}",
                Collections.emptyList());
    }

    public void testFinalModifierCompletionForLocalVariable() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    private void get() {\n"
                + "        |int count = 0;\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    private void get() {\n"
                + "        final int count = 0;\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testAbstractModifierCompletionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public static abstract class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testFinalModifierCompletionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public static class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public static final class Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testAccessModifierCompletionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |static class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    static class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticAndStrictfpModifierCompletionForClassDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticAndStrictfpModifierCompletionInZeroModifierPositionForClassDeclaredInsideEnum()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public class Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public class Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testAbstractModifierCompletionForInterfaceDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public static interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public static abstract interface Inner {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAccessModifierCompletionForInterfaceDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |static interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    static interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticAndStrictfpModifierCompletionForInterfaceDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticAndStrictfpModifierCompletionInZeroModifierPositionForInterfaceDeclaredInsideEnum()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public interface Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public interface Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testAccessModifierCompletionForEnumDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForEnumDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |static enum Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    static enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticAndStrictfpModifierCompletionForEnumDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |enum Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testStaticAndStrictfpModifierCompletionInZeroModifierPositionForEnumDeclaredInsideEnum()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    |public enum Inner {\n"
                + "    }\n"
                + "}",
                "enum Outer {\n"
                + "    TEST;\n"
                + "    public enum Inner {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp"));
    }

    public void testAccessModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |static final void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    static final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAbstractModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    abstract void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "a",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |protected void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    protected abstract void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testFinalModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    public static final void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testNativeModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "n",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testNativeModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "n",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |public static void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    public static native void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("native"));
    }

    public void testSynchronizedModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testSynchronizedModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testStrictfpModifierCompletionInZeroModifierPositionForMethodDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |public final void test() {\n"
                + "    }\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    public final void test() {\n"
                + "    }\n"
                + "}",
                Arrays.asList("static", "strictfp", "synchronized"));
    }

    public void testAccessModifierCompletionForFieldForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testAccessModifierCompletionInZeroModifierPositionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "p",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |static final int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    static final int count;\n"
                + "}",
                Arrays.asList("private", "protected", "public"));
    }

    public void testStaticModifierCompletionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    static int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testStaticModifierCompletionInZeroModifierPositionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "s",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |private transient int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("static"));
    }

    public void testFinalModifierCompletionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    final int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testFinalModifierCompletionInZeroModifierPositionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "f",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |private transient int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    private final transient int count;\n"
                + "}",
                Collections.singletonList("final"));
    }

    public void testTransientModifierCompletionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "t",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testTransientModifierCompletionInZeroModifierPositionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "t",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |private static int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    private static transient int count;\n"
                + "}",
                Collections.singletonList("transient"));
    }

    public void testVolatileModifierCompletionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "v",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testVolatileModifierCompletionInZeroModifierPositionForFieldDeclaredInsideEnum() throws IOException {
        doAbbreviationInsert(
                "v",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |private static int count;\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "    private static volatile int count;\n"
                + "}",
                Collections.singletonList("volatile"));
    }

    public void testPublicModifierCompletionForMethodDeclaredInsideInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "interface Test {\n"
                + "    |void test();\n"
                + "}",
                "interface Test {\n"
                + "    public void test();\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testPublicModifierCompletionInZeroModifierPositionForMethodDeclaredInsideInterface() throws IOException {
        doAbbreviationInsert(
                "p",
                "interface Test {\n"
                + "    |abstract void test();\n"
                + "}",
                "interface Test {\n"
                + "    public abstract void test();\n"
                + "}",
                Collections.singletonList("public"));
    }

    public void testAbstractModifierCompletionForMethodDeclaredInsideInterface() throws IOException {
        doAbbreviationInsert(
                "a",
                "interface Test {\n"
                + "    |void test();\n"
                + "}",
                "interface Test {\n"
                + "    abstract void test();\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testAbstractModifierCompletionInZeroModifierPositionForMethodDeclaredInsideInterface() throws
            IOException {
        doAbbreviationInsert(
                "a",
                "interface Test {\n"
                + "    |public void test();\n"
                + "}",
                "interface Test {\n"
                + "    public abstract void test();\n"
                + "}",
                Collections.singletonList("abstract"));
    }

    public void testStaticSynchronizedAndStrictfpModifierCompletionInZeroModifierPositionForMethodDeclaredInsideInterface()
            throws IOException {
        doAbbreviationInsert(
                "s",
                "interface Test {\n"
                + "    |public void test() {\n"
                + "    }\n"
                + "}",
                "interface Test {\n"
                + "    public static void test() {\n"
                + "    }\n"
                + "}",
                Collections.singletonList("static"));
    }

    @Override
    protected void tearDown() throws Exception {
        after();
    }
}
