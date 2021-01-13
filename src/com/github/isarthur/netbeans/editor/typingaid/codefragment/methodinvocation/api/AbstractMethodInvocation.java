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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.api;

import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractMethodInvocation implements MethodInvocation, Comparable<AbstractMethodInvocation> {

    protected final ElementHandle<ExecutableElement> method;
    protected final List<ExpressionTree> arguments;
    protected String text;

    public AbstractMethodInvocation(ElementHandle<ExecutableElement> method, List<ExpressionTree> arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public ElementHandle<ExecutableElement> getMethod() {
        return method;
    }

    @Override
    public List<ExpressionTree> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        String methodAbbreviation = StringUtilities.getMethodAbbreviation(method.getBinaryName());
        return methodAbbreviation.equals(abbreviation);
    }

    @Override
    public int compareTo(AbstractMethodInvocation other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        switch (request.getCurrentKind()) {
            case BLOCK:
            case CASE:
            case SWITCH:
                if (JavaSourceUtilities.isMethodReturnVoid(method.resolve(request.getWorkingCopy()))) {
                    return JavaSourceMaker.makeVoidMethodInvocationStatementTree(this, request);
                } else {
                    return JavaSourceMaker.makeMethodInvocationStatementTree(this, request);
                }
            default:
                return JavaSourceMaker.makeMethodInvocationExpressionTree(this, request);
        }
    }

    @Override
    public String toString() {
        return text;
    }
}
