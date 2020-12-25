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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.NormalMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.Tree;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.api.java.source.TypeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodInvocationCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        List<Element> localElements = JavaSourceUtilities.getElementsByAbbreviation(copy, request.getAbbreviation());
        localElements.forEach(element -> collectMethodInvocations(element, JavaSourceUtilities.getNonStaticMethodsInClassHierarchy(element, copy), request));
        super.collect(request);
    }

    private void collectMethodInvocations(
            Element scope, List<ExecutableElement> methods, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Tree.Kind currentContext = request.getCurrentPath().getLeaf().getKind();
        if (currentContext == Tree.Kind.PARAMETERIZED_TYPE) {
            return;
        }
        methods = JavaSourceUtilities.getMethodsByAbbreviation(methods, request.getAbbreviation());
        List<CodeFragment> codeFragments = request.getCodeFragments();
        methods.forEach(method -> {
            if (currentContext != Tree.Kind.BLOCK) {
                TypeUtilities typeUtilities = copy.getTypeUtilities();
                String typeName = typeUtilities.getTypeName(method.getReturnType()).toString();
                if (!typeName.equals("void")) { //NOI18N
                    NormalMethodInvocation methodInvocation = new NormalMethodInvocation(
                            scope, method, JavaSourceUtilities.evaluateMethodArguments(method, request));
                    if (JavaSourceUtilities.isMethodReturnVoid(method)) {
                        methodInvocation.setText(
                                JavaSourceMaker.makeVoidMethodInvocationStatementTree(methodInvocation, request).toString());
                    } else {
                        methodInvocation.setText(JavaSourceMaker.makeMethodInvocationExpressionTree(
                                methodInvocation, request).toString());
                    }
                    codeFragments.add(methodInvocation);
                }
            } else {
                NormalMethodInvocation methodInvocation = new NormalMethodInvocation(
                        scope, method, JavaSourceUtilities.evaluateMethodArguments(method, request));
                if (JavaSourceUtilities.isMethodReturnVoid(method)) {
                    methodInvocation.setText(
                            JavaSourceMaker.makeVoidMethodInvocationStatementTree(methodInvocation, request).toString());
                } else {
                    methodInvocation.setText(JavaSourceMaker.makeMethodInvocationStatementTree(
                            methodInvocation, request) + ";"); //NOI18N
                }
                codeFragments.add(methodInvocation);
            }
        });
    }
}
