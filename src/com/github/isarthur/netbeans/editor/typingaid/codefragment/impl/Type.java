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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class Type implements CodeFragment, Comparable<Type> {

    private final TypeElement type;

    public Type(TypeElement type) {
        this.type = type;
    }

    public TypeElement getType() {
        return type;
    }

    public String getSimpleName() {
        String qualifiedName = type.getQualifiedName().toString();
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return qualifiedName.substring(lastDotIndex + 1);
        }
        return qualifiedName;
    }

    @Override
    public Kind getKind() {
        return Kind.TYPE;
    }

    @Override
    public int compareTo(Type other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return type.getQualifiedName().toString();
    }
}
