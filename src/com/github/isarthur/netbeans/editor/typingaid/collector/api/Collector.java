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
package com.github.isarthur.netbeans.editor.typingaid.collector.api;

import com.github.isarthur.netbeans.editor.typingaid.Request;

/**
 *
 * @author Arthur Sadykov
 */
public interface Collector {

    void collect(Request request);

    void setNext(Collector nextCollector);

    Kind getKind();

    enum Kind {
        CHAINED_ENUM_CONSTANT_ACCESS,
        CHAINED_FIELD_ACCESS,
        CHAINED_METHOD_INVOCATION,
        ENUM_CONSTANT,
        EXCEPTION_PARAMETER,
        FIELD,
        IMPORTED_TYPE,
        KEYWORD,
        LOCAL_VARIABLE,
        LOCAL_METHOD_INVOCATION,
        INTERNAL_TYPE,
        LITERAL,
        METHOD_INVOCATION,
        MODIFIER,
        PARAMETER,
        PRIMITIVE_TYPE,
        RESOURCE_VARIABLE,
        SAME_PACKAGE_TYPE,
        STATIC_FIELD_ACCESS,
        STATIC_FIELD_ACCESS_FOR_IMPORTED_TYPES,
        STATIC_METHOD_INVOCATION,
        STATIC_METHOD_INVOCATION_FOR_IMPORTED_TYPES,
        EXTERNAL_TYPE;
    }
}
