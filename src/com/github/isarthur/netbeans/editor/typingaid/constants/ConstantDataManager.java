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

import com.github.isarthur.netbeans.editor.typingaid.codefragment.Keyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.PrimitiveType;
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
    public static final String LONG_ZERO_LITERAL = "0L"; //NOI18N
    public static final Set<Keyword> KEYWORDS;
    public static final Set<String> MODIFIERS;
    public static final Set<PrimitiveType> PRIMITIVE_TYPES;
    public static final String NULL = "null"; //NOI18N
    public static final String PARENTHESIZED_ERROR = "(ERROR)"; //NOI18N
    public static final String SHOULD_SET_CARET_POSITION_AND_COLLECT_LOCAL_ELEMENTS =
            "The current caret position and local elements should be collected before calling this method!"; //NOI18N
    public static final String SPACE = " "; //NOI18N
    public static final String STATE_IS_NOT_IN_RESOLVED_PHASE = "Could not move state to Phase.RESOLVED!"; //NOI18N
    public static final String STATE_IS_NOT_IN_PARSED_PHASE = "Could not move state to Phase.PARSED!"; //NOI18N
    public static final String SUPER = "super"; //NOI18N
    public static final String THIS = "this"; //NOI18N
    private static final int KEYWORD_COUNT = 32;
    private static final int MODIFIER_COUNT = 11;
    private static final int PRIMITIVE_TYPE_COUNT = 8;

    private ConstantDataManager() {
    }

    static {
        KEYWORDS = new HashSet<>(KEYWORD_COUNT);
        KEYWORDS.add(new Keyword("assert")); //NOI18N
        KEYWORDS.add(new Keyword("break")); //NOI18N
        KEYWORDS.add(new Keyword("case")); //NOI18N
        KEYWORDS.add(new Keyword("catch")); //NOI18N
        KEYWORDS.add(new Keyword("class")); //NOI18N
        KEYWORDS.add(new Keyword("continue")); //NOI18N
        KEYWORDS.add(new Keyword("default")); //NOI18N
        KEYWORDS.add(new Keyword("do")); //NOI18N
        KEYWORDS.add(new Keyword("else")); //NOI18N
        KEYWORDS.add(new Keyword("enum")); //NOI18N
        KEYWORDS.add(new Keyword("extends")); //NOI18N
        KEYWORDS.add(new Keyword("false")); //NOI18N
        KEYWORDS.add(new Keyword("finally")); //NOI18N
        KEYWORDS.add(new Keyword("for")); //NOI18N
        KEYWORDS.add(new Keyword("if")); //NOI18N
        KEYWORDS.add(new Keyword("instanceof")); //NOI18N
        KEYWORDS.add(new Keyword("interface")); //NOI18N
        KEYWORDS.add(new Keyword("implements")); //NOI18N
        KEYWORDS.add(new Keyword("import")); //NOI18N
        KEYWORDS.add(new Keyword("null")); //NOI18N
        KEYWORDS.add(new Keyword("Object")); //NOI18N
        KEYWORDS.add(new Keyword("return")); //NOI18N
        KEYWORDS.add(new Keyword("String")); //NOI18N
        KEYWORDS.add(new Keyword("super")); //NOI18N
        KEYWORDS.add(new Keyword("switch")); //NOI18N
        KEYWORDS.add(new Keyword("this")); //NOI18N
        KEYWORDS.add(new Keyword("throw")); //NOI18N
        KEYWORDS.add(new Keyword("throws")); //NOI18N
        KEYWORDS.add(new Keyword("true")); //NOI18N
        KEYWORDS.add(new Keyword("try")); //NOI18N
        KEYWORDS.add(new Keyword("void")); //NOI18N
        KEYWORDS.add(new Keyword("while")); //NOI18N
        MODIFIERS = new HashSet<>(MODIFIER_COUNT);
        MODIFIERS.add("abstract"); //NOI18N
        MODIFIERS.add("final"); //NOI18N
        MODIFIERS.add("native"); //NOI18N
        MODIFIERS.add("private"); //NOI18N
        MODIFIERS.add("protected"); //NOI18N
        MODIFIERS.add("public"); //NOI18N
        MODIFIERS.add("static"); //NOI18N
        MODIFIERS.add("strictfp"); //NOI18N
        MODIFIERS.add("synchronized"); //NOI18N
        MODIFIERS.add("transient"); //NOI18N
        MODIFIERS.add("volatile"); //NOI18N
        PRIMITIVE_TYPES = new HashSet<>(PRIMITIVE_TYPE_COUNT);
        PRIMITIVE_TYPES.add(new PrimitiveType("boolean")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("byte")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("char")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("double")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("float")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("int")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("long")); //NOI18N
        PRIMITIVE_TYPES.add(new PrimitiveType("short")); //NOI18N
    }
}
