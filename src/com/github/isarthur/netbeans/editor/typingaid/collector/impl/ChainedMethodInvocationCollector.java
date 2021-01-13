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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.ChainedMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.context.api.CodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.context.impl.CodeCompletionContextFactory;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.util.TreePath;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class ChainedMethodInvocationCollector extends AbstractCodeFragmentCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        TreePath currentPath = request.getCurrentPath();
        CodeCompletionContext context =
                CodeCompletionContextFactory.getCodeCompletionContext(currentPath.getLeaf().getKind());
        TypeMirror type = context.getType(request);
        if (type == null) {
            return;
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return;
        }
        List<ExecutableElement> methods = JavaSourceUtilities.getMethodsInClassHierarchy(typeElement, copy);
        methods = JavaSourceUtilities.getMethodsByAbbreviation(methods, request.getAbbreviation());
        List<CodeFragment> codeFragments = request.getCodeFragments();
        methods.forEach(method -> {
            ChainedMethodInvocation methodInvocation = new ChainedMethodInvocation(
                    ElementHandle.create(method), JavaSourceUtilities.evaluateMethodArguments(method, request));
            methodInvocation.setText(JavaSourceMaker.makeMethodInvocationExpressionTree(
                    methodInvocation, request).toString());
            codeFragments.add(methodInvocation);
        });
        super.collect(request);
    }
}
