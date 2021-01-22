/*
 * Copyright 2021 Arthur Sadykov.
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
package com.github.isarthur.netbeans.editor.typingaid.selector.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.selector.api.CodeFragmentSelector;

/**
 *
 * @author Arthur Sadykov
 */
public class CodeFragmentSelectorFactory {

    private CodeFragmentSelectorFactory() {
    }

    public static CodeFragmentSelector getCodeFragmentSelector(CodeFragment codeFragment) {
        switch (codeFragment.getKind()) {
            case ASSERT_KEYWORD:
            case CASE_KEYWORD:
            case DO_KEYWORD:
            case ELSE_KEYWORD:
            case IF_KEYWORD:
            case IMPORT_KEYWORD:
            case RETURN_KEYWORD:
            case SWITCH_KEYWORD:
            case SYNCHRONIZED_KEYWORD:
            case THROW_KEYWORD:
            case WHILE_KEYWORD:
                return new ExpressionCodeFragmentSelector();
            case BOOLEAN_PRIMITIVE_TYPE:
            case BYTE_PRIMITIVE_TYPE:
            case CATCH_KEYWORD:
            case CHAR_PRIMITIVE_TYPE:
            case CLASS_KEYWORD:
            case ENUM_KEYWORD:
            case DOUBLE_PRIMITIVE_TYPE:
            case FLOAT_PRIMITIVE_TYPE:
            case INNER_TYPE:
            case INSTANCEOF_KEYWORD:
            case INT_PRIMITIVE_TYPE:
            case INTERFACE_KEYWORD:
            case LOCAL_ELEMENT:
            case LONG_PRIMITIVE_TYPE:
            case NEW_KEYWORD:
            case NORMAL_METHOD_INVOCATION:
            case LOCAL_METHOD_INVOCATION:
            case SHORT_PRIMITIVE_TYPE:
            case STATIC_METHOD_INVOCATION:
            case TRY_KEYWORD:
            case TYPE:
            case VOID_KEYWORD:
                return new IdentifierCodeFragmentSelector();
            case CHAINED_METHOD_INVOCATION:
                return new IdentifierOrLiteralInChainedMethodCodeFragmentSelector();
            case FOR_KEYWORD:
                return new IntLiteralCodeFragmentSelector();
            default:
                return new NullCodeFragmentSelector();
        }
    }
}
