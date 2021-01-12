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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.api;

import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.Tree;

/**
 *
 * @author Arthur Sadykov
 */
public interface CodeFragment {

    Kind getKind();

    boolean isAbbreviationEqualTo(String abbreviation);

    Tree getTreeToInsert(CodeCompletionRequest request);

    public enum Kind {
        ABSTRACT_MODIFIER,
        ASSERT_KEYWORD,
        BOOLEAN_PRIMITIVE_TYPE,
        BREAK_KEYWORD,
        BYTE_PRIMITIVE_TYPE,
        CASE_KEYWORD,
        CATCH_KEYWORD,
        CHAINED_FIELD_ACCESS,
        CHAINED_METHOD_INVOCATION,
        CHAR_PRIMITIVE_TYPE,
        CLASS_KEYWORD,
        CONTINUE_KEYWORD,
        DEFAULT_KEYWORD,
        DO_KEYWORD,
        DOUBLE_PRIMITIVE_TYPE,
        ELSE_KEYWORD,
        ENUM_KEYWORD,
        EXTENDS_KEYWORD,
        EXTERNAL_INNER_TYPE,
        EXTERNAL_TYPE,
        FALSE_LITERAL,
        FIELD_ACCESS,
        FINAL_MODIFIER,
        FINALLY_KEYWORD,
        FLOAT_PRIMITIVE_TYPE,
        FOR_KEYWORD,
        GLOBAL_INNER_TYPE,
        GLOBAL_TYPE,
        IF_KEYWORD,
        IMPLEMENTS_KEYWORD,
        IMPORT_KEYWORD,
        INSTANCEOF_KEYWORD,
        INT_PRIMITIVE_TYPE,
        INTERFACE_KEYWORD,
        INTERNAL_TYPE,
        LOCAL_ELEMENT,
        LOCAL_FIELD_ACCESS,
        LOCAL_METHOD_INVOCATION,
        LONG_PRIMITIVE_TYPE,
        NATIVE_MODIFIER,
        NEW_KEYWORD,
        NORMAL_METHOD_INVOCATION,
        NULL_LITERAL,
        PRIVATE_MODIFIER,
        PROTECTED_MODIFIER,
        PUBLIC_MODIFIER,
        RETURN_KEYWORD,
        SHORT_PRIMITIVE_TYPE,
        STATEMENT,
        STATIC_FIELD_ACCESS,
        STATIC_METHOD_INVOCATION,
        STATIC_MODIFIER,
        STRICTFP_MODIFIER,
        SWITCH_KEYWORD,
        SYNCHRONIZED_MODIFIER,
        THIS_KEYWORD,
        THROW_KEYWORD,
        THROWS_KEYWORD,
        TRANSIENT_MODIFIER,
        TRUE_LITERAL,
        TRY_KEYWORD,
        VOID_KEYWORD,
        VOLATILE_MODIFIER,
        WHILE_KEYWORD
    }

    void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request);
}
