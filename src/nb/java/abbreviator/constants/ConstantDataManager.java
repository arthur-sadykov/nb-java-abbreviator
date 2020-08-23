/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.constants;

import java.util.Arrays;
import java.util.List;

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
}
