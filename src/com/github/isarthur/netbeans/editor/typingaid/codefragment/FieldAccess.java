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
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class FieldAccess implements CodeFragment, Comparable<FieldAccess> {

    private TypeElement scope;
    private Element name;

    public FieldAccess(TypeElement type, Element constant) {
        this.scope = type;
        this.name = constant;
    }

    public Element getName() {
        return name;
    }

    public void setName(Element name) {
        this.name = name;
    }

    public TypeElement getScope() {
        return scope;
    }

    public void setScope(TypeElement scope) {
        this.scope = scope;
    }

    @Override
    public Kind getKind() {
        return Kind.FIELD_ACCESS;
    }

    @Override
    public int compareTo(FieldAccess other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return scope + "." + name; //NOI18N
    }
}
