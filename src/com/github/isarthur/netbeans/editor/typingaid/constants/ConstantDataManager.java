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
import java.util.List;

/**
 *
 * @author Arthur Sadykov
 */
public class ConstantDataManager {

    public static final String ANGLED_ERROR = "<error>"; //NOI18N
    public static final String ARGUMENT_TAG = "argumentTag"; //NOI18N
    public static final String BOOLEAN = "boolean"; //NOI18N
    public static final String BYTE = "byte"; //NOI18N
    public static final String CHAR = "char"; //NOI18N
    public static final String DOUBLE = "double"; //NOI18N
    public static final String EMPTY_CHAR = "' '"; //NOI18N
    public static final String EMPTY_STRING = "\"\""; //NOI18N
    public static final String EXPRESSION_TAG = "expressionTag"; //NOI18N
    public static final String FALSE = "false"; //NOI18N
    public static final String FIRST_IDENTIFIER_OR_LITERAL_TAG = "firstIdentifierOrLiteralTag"; //NOI18N
    public static final String FLOAT = "float"; //NOI18N
    public static final List<Character> FORBIDDEN_FIRST_CHARS = Arrays.asList('~', '!', '@', '#', '$', '%', '^', '&',
            '*', '(', ')', '-', '+', '_', '=', '/', '{', '}', '[', ']', '|', '\\', ':', ';', '\'', '"', '<', '>', ',',
            '.', '?');
    public static final String INT = "int"; //NOI18N
    public static final String JAVA_SOURCE_NOT_ASSOCIATED_TO_DOCUMENT = "The Java source is not associated to document!"; //NOI18N
    public static final String LONG = "long"; //NOI18N
    public static final String NULL = "null"; //NOI18N
    public static final String PARENTHESIZED_ERROR = "(ERROR)"; //NOI18N
    public static final String SECOND_IDENTIFIER_OR_LITERAL_TAG = "secondIdentifierOrLiteralTag"; //NOI18N
    public static final String SECOND_INT_LITERAL_TAG = "secondIntLiteralTag"; //NOI18N
    public static final String SHORT = "short"; //NOI18N
    public static final String STRING = "java.lang.String"; //NOI18N
    public static final String STATE_IS_NOT_IN_PARSED_PHASE = "Cannot move state to Phase.PARSED!"; //NOI18N
    public static final String STATE_IS_NOT_IN_RESOLVED_PHASE = "Cannot move state to Phase.RESOLVED!"; //NOI18N
    public static final String SUPER = "super"; //NOI18N
    public static final String THIS = "this"; //NOI18N
    public static final String TRUE = "true"; //NOI18N
    public static final String VOID = "void"; //NOI18N
    public static final String ZERO = "0"; //NOI18N
    public static final String ZERO_DOT_ZERO = "0.0"; //NOI18N
    public static final String ZERO_DOT_ZERO_F = "0.0F"; //NOI18N
    public static final String ZERO_L = "0L"; //NOI18N

    private ConstantDataManager() {
    }
}
