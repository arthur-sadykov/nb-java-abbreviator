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
 * limitations under the License.I
 */
package com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.api.AbstractMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.ExpressionTree;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author Arthur Sadykov
 */
public class ChainedMethodInvocation extends AbstractMethodInvocation {

    public ChainedMethodInvocation(ElementHandle<ExecutableElement> method, List<ExpressionTree> arguments) {
        super(method, arguments);
    }

    @Override
    public void accept(CodeFragmentInsertVisitor visitor, CodeCompletionRequest request) {
        visitor.visit(this, request);
    }

    @Override
    public Kind getKind() {
        return Kind.CHAINED_METHOD_INVOCATION;
    }
}
