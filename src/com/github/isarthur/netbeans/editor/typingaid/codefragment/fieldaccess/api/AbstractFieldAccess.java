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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.api;

import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.Tree;
import javax.lang.model.element.Element;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractFieldAccess implements FieldAccess, Comparable<AbstractFieldAccess> {

    protected Element identifier;

    public AbstractFieldAccess(Element identifier) {
        this.identifier = identifier;
    }

    @Override
    public Element getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        String identifierAbbreviation = StringUtilities.getElementAbbreviation(identifier.getSimpleName().toString());
        return abbreviation.equals(identifierAbbreviation);
    }

    @Override
    public int compareTo(AbstractFieldAccess other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return identifier.getSimpleName().toString();
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        return null;
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }
}
