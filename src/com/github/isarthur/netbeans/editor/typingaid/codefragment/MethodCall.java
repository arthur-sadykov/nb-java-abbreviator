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
import com.github.isarthur.netbeans.editor.typingaid.JavaSourceHelper;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodCall implements CodeFragment {

    private final Element scope;
    private final ExecutableElement method;
    private final List<ExpressionTree> arguments;
    private final int argumentsNumber;
    private final JavaSourceHelper helper;

    public MethodCall(Element element, ExecutableElement method, List<ExpressionTree> arguments,
            JavaSourceHelper helper) {
        this.scope = element;
        this.method = method;
        this.arguments = arguments;
        this.argumentsNumber = method.getParameters().size();
        this.helper = helper;
    }

    public Element getScope() {
        return scope;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public List<ExpressionTree> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public int getArgumentsNumber() {
        return argumentsNumber;
    }

    @Override
    public String toString() {
        Tree methodCall;
        if (helper.isMethodReturnVoid(method)) {
            methodCall = helper.createVoidMethodCall(this);
            return methodCall.toString();
        } else {
            if (scope == null) {
                methodCall = helper.createMethodCallWithoutReturnValue(this);
                return methodCall.toString();
            } else {
                methodCall = helper.createMethodCallWithReturnValue(this);
                return methodCall.toString() + ";"; //NOI18N
            }
        }
    }
}
