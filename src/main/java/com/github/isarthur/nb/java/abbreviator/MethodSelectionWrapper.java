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
package com.github.isarthur.nb.java.abbreviator;

import com.sun.source.tree.ExpressionTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodSelectionWrapper {

    private Element element;
    private final ExecutableElement method;
    private final List<ExpressionTree> arguments;
    private final int argumentsNumber;
    private int resolvedArgumentsNumber;
    private double relation;
    private final boolean staticMember;

    public MethodSelectionWrapper(Element element, ExecutableElement method, List<ExpressionTree> arguments,
            boolean staticMember) {
        this.element = element;
        this.method = method;
        this.arguments = arguments;
        this.argumentsNumber = method.getParameters().size();
        this.staticMember = staticMember;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
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

    public int getResolvedArgumentsNumber() {
        return resolvedArgumentsNumber;
    }

    public double getRelation() {
        return relation;
    }

    public void setResolvedArgumentsNumber(int resolvedArgumentsNumber) {
        this.resolvedArgumentsNumber = resolvedArgumentsNumber;
        this.relation = argumentsNumber == 0 ? 0 : this.resolvedArgumentsNumber / argumentsNumber;
    }

    public boolean isStaticMember() {
        return staticMember;
    }
}
