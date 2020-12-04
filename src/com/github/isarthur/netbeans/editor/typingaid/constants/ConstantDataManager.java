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
package com.github.isarthur.netbeans.editor.typingaid.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Arthur Sadykov
 */
public class ConstantDataManager {

    public static final String ANGLED_ERROR = "<error>"; //NOI18N
    public static final String ARGUMENT_MUST_BE_NON_NULL = "The argument %s must be non-null!"; //NOI18N
    public static final String DOUBLE_ZERO_LITERAL = "0.0"; //NOI18N
    public static final String EMPTY_STRING = ""; //NOI18N
    public static final String FALSE = "false"; //NOI18N
    public static final String FLOAT_ZERO_LITERAL = "0.0F"; //NOI18N
    public static final List<Character> FORBIDDEN_FIRST_CHARS = Arrays.asList('~', '!', '@', '#', '$', '%', '^', '&',
            '*', '(', ')', '-', '+', '_', '=', '/', '{', '}', '[', ']', '|', '\\', ':', ';', '\'', '"', '<', '>', ',',
            '.', '?');
    public static final String INTEGER_ZERO_LITERAL = "0"; //NOI18N
    public static final String INVALID_CHARS_COUNT = "Invalid characters count!"; //NOI18N
    public static final String INVALID_POSITION = "The 'position' argument is out of bounds of document!"; //NOI18N
    public static final String JAVA_SOURCE_NOT_ASSOCIATED_TO_DOCUMENT = "The Java source is not associated to document!"; //NOI18N
    public static final Set<String> KEYWORDS;
    public static final String LONG_ZERO_LITERAL = "0L"; //NOI18N
    public static final String NULL = "null"; //NOI18N
    public static final String PARENTHESIZED_ERROR = "(ERROR)"; //NOI18N
    public static final String SHOULD_SET_CARET_POSITION_AND_COLLECT_LOCAL_ELEMENTS =
            "The current caret position and local elements should be collected before calling this method!"; //NOI18N
    public static final String SPACE = " "; //NOI18N
    public static final String STATE_IS_NOT_IN_RESOLVED_PHASE = "Could not move state to Phase.RESOLVED!"; //NOI18N
    public static final String SUPER = "super"; //NOI18N
    public static final String THIS = "this"; //NOI18N
    private static final int KEYWORD_COUNT = 49;

    private ConstantDataManager() {
    }

    static {
        KEYWORDS = new HashSet<>(KEYWORD_COUNT);
        KEYWORDS.add("abstract"); //NOI18N
        KEYWORDS.add("assert"); //NOI18N
        KEYWORDS.add("boolean"); //NOI18N
        KEYWORDS.add("break"); //NOI18N
        KEYWORDS.add("byte"); //NOI18N
        KEYWORDS.add("char"); //NOI18N
        KEYWORDS.add("case"); //NOI18N
        KEYWORDS.add("class"); //NOI18N
        KEYWORDS.add("continue"); //NOI18N
        KEYWORDS.add("double"); //NOI18N
        KEYWORDS.add("default"); //NOI18N
        KEYWORDS.add("else"); //NOI18N
        KEYWORDS.add("enum"); //NOI18N
        KEYWORDS.add("extends"); //NOI18N
        KEYWORDS.add("false"); //NOI18N
        KEYWORDS.add("final"); //NOI18N
        KEYWORDS.add("float"); //NOI18N
        KEYWORDS.add("finally"); //NOI18N
        KEYWORDS.add("if"); //NOI18N
        KEYWORDS.add("int"); //NOI18N
        KEYWORDS.add("implements"); //NOI18N
        KEYWORDS.add("interface"); //NOI18N
        KEYWORDS.add("instanceof"); //NOI18N
        KEYWORDS.add("import"); //NOI18N
        KEYWORDS.add("long"); //NOI18N
        KEYWORDS.add("null"); //NOI18N
        KEYWORDS.add("native"); //NOI18N
        KEYWORDS.add("Object"); //NOI18N
        KEYWORDS.add("package"); //NOI18N
        KEYWORDS.add("private"); //NOI18N
        KEYWORDS.add("protected"); //NOI18N
        KEYWORDS.add("public"); //NOI18N
        KEYWORDS.add("return"); //NOI18N
        KEYWORDS.add("strictfp"); //NOI18N
        KEYWORDS.add("short"); //NOI18N
        KEYWORDS.add("static"); //NOI18N
        KEYWORDS.add("super"); //NOI18N
        KEYWORDS.add("switch"); //NOI18N
        KEYWORDS.add("synchronized"); //NOI18N
        KEYWORDS.add("true"); //NOI18N
        KEYWORDS.add("this"); //NOI18N
        KEYWORDS.add("transient"); //NOI18N
        KEYWORDS.add("throw"); //NOI18N
        KEYWORDS.add("throws"); //NOI18N
        KEYWORDS.add("void"); //NOI18N
        KEYWORDS.add("volatile"); //NOI18N
        KEYWORDS.add("while"); //NOI18N
    }
}
