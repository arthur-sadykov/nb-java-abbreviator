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
package com.github.isarthur.nb.java.abbreviator.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final Map<String, String> ABBREVIATION_TO_KEYWORD;
    public static final String LONG_ZERO_LITERAL = "0L"; //NOI18N
    public static final String NULL = "null"; //NOI18N
    public static final String PARENTHESIZED_ERROR = "(ERROR)"; //NOI18N
    public static final String SHOULD_SET_CARET_POSITION_AND_COLLECT_LOCAL_ELEMENTS =
            "The current caret position and local elements should be collected before calling this method!"; //NOI18N
    public static final String SPACE = " "; //NOI18N
    public static final String STATE_IS_NOT_IN_RESOLVED_PHASE = "Could not move state to Phase.RESOLVED!"; //NOI18N
    public static final String SUPER = "super"; //NOI18N
    public static final String THIS = "this"; //NOI18N

    private ConstantDataManager() {
    }

    static {
        ABBREVIATION_TO_KEYWORD = new HashMap<>();
        ABBREVIATION_TO_KEYWORD.put("ab", "abstract");
        ABBREVIATION_TO_KEYWORD.put("as", "assert");
        ABBREVIATION_TO_KEYWORD.put("b", "boolean");
        ABBREVIATION_TO_KEYWORD.put("br", "break");
        ABBREVIATION_TO_KEYWORD.put("by", "byte");
        ABBREVIATION_TO_KEYWORD.put("c", "char");
        ABBREVIATION_TO_KEYWORD.put("ca", "case");
        ABBREVIATION_TO_KEYWORD.put("cl", "class");
        ABBREVIATION_TO_KEYWORD.put("co", "continue");
        ABBREVIATION_TO_KEYWORD.put("d", "double");
        ABBREVIATION_TO_KEYWORD.put("de", "default");
        ABBREVIATION_TO_KEYWORD.put("el", "else");
        ABBREVIATION_TO_KEYWORD.put("en", "enum");
        ABBREVIATION_TO_KEYWORD.put("ex", "extends");
        ABBREVIATION_TO_KEYWORD.put("f", "false");
        ABBREVIATION_TO_KEYWORD.put("fi", "final");
        ABBREVIATION_TO_KEYWORD.put("fl", "float");
        ABBREVIATION_TO_KEYWORD.put("fy", "finally");
        ABBREVIATION_TO_KEYWORD.put("i", "int");
        ABBREVIATION_TO_KEYWORD.put("im", "implements");
        ABBREVIATION_TO_KEYWORD.put("in", "interface");
        ABBREVIATION_TO_KEYWORD.put("io", "instanceof");
        ABBREVIATION_TO_KEYWORD.put("it", "import");
        ABBREVIATION_TO_KEYWORD.put("l", "long");
        ABBREVIATION_TO_KEYWORD.put("n", "null");
        ABBREVIATION_TO_KEYWORD.put("na", "native");
        ABBREVIATION_TO_KEYWORD.put("o", "Object");
        ABBREVIATION_TO_KEYWORD.put("pa", "package");
        ABBREVIATION_TO_KEYWORD.put("pr", "private");
        ABBREVIATION_TO_KEYWORD.put("pro", "protected");
        ABBREVIATION_TO_KEYWORD.put("pu", "public");
        ABBREVIATION_TO_KEYWORD.put("re", "return");
        ABBREVIATION_TO_KEYWORD.put("s", "String");
        ABBREVIATION_TO_KEYWORD.put("sf", "strictfp");
        ABBREVIATION_TO_KEYWORD.put("sh", "short");
        ABBREVIATION_TO_KEYWORD.put("st", "static");
        ABBREVIATION_TO_KEYWORD.put("su", "super");
        ABBREVIATION_TO_KEYWORD.put("sw", "switch");
        ABBREVIATION_TO_KEYWORD.put("sy", "synchronized");
        ABBREVIATION_TO_KEYWORD.put("t", "true");
        ABBREVIATION_TO_KEYWORD.put("th", "this");
        ABBREVIATION_TO_KEYWORD.put("tr", "transient");
        ABBREVIATION_TO_KEYWORD.put("tw", "throw");
        ABBREVIATION_TO_KEYWORD.put("ts", "throws");
        ABBREVIATION_TO_KEYWORD.put("v", "void");
        ABBREVIATION_TO_KEYWORD.put("vo", "volatile");
        ABBREVIATION_TO_KEYWORD.put("wh", "while");
    }
}
