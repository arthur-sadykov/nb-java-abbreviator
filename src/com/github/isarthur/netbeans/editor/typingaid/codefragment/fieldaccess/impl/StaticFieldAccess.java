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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.api.AbstractFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.Tree;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author Arthur Sadykov
 */
public class StaticFieldAccess extends AbstractFieldAccess {

    private final TypeElement scope;

    public StaticFieldAccess(TypeElement scope, Element identifier) {
        super(identifier);
        this.scope = scope;
    }

    @Override
    public Kind getKind() {
        return Kind.STATIC_FIELD_ACCESS;
    }

    public Element getScope() {
        return scope;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        String scopeAbbreviation = StringUtilities.getElementAbbreviation(scope.getSimpleName().toString());
        String identifierAbbreviation = StringUtilities.getElementAbbreviation(identifier.getSimpleName().toString());
        return abbreviation.equals(scopeAbbreviation + "." + identifierAbbreviation); //NOI18N
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        return JavaSourceMaker.makeMemberSelectTree(
                JavaSourceMaker.makeQualIdentTree(scope, request),
                identifier,
                request);
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public String toString() {
        return scope.getQualifiedName() + "." + identifier.getSimpleName(); //NOI18N
    }
}
