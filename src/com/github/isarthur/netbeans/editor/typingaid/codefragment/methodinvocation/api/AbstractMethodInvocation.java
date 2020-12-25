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

import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ExpressionTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ExecutableElement;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractMethodInvocation implements MethodInvocation, Comparable<AbstractMethodInvocation> {

    protected final ExecutableElement method;
    protected final List<ExpressionTree> arguments;
    protected String text;

    public AbstractMethodInvocation(ExecutableElement method, List<ExpressionTree> arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public ExecutableElement getMethod() {
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
        String methodAbbreviation = StringUtilities.getMethodAbbreviation(method.getSimpleName().toString());
        return methodAbbreviation.equals(abbreviation);
    }

    @Override
    public int compareTo(AbstractMethodInvocation other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return text;
    }
}
