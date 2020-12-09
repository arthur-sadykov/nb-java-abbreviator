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
package com.github.isarthur.netbeans.editor.typingaid.codefragment;

import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;

/**
 *
 * @author Arthur Sadykov
 */
public class PrimitiveType implements CodeFragment, Comparable<PrimitiveType> {

    private final String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(name).equals(abbreviation);
    }

    @Override
    public Kind getKind() {
        return Kind.PRIMITIVE_TYPE;
    }

    @Override
    public int compareTo(PrimitiveType other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return name;
    }
}
