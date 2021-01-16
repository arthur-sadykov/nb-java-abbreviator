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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragmentCollectAndInsertHandler;
import com.github.isarthur.netbeans.editor.typingaid.context.api.CodeCompletionContext;
import com.github.isarthur.netbeans.editor.typingaid.context.impl.CodeCompletionContextFactory;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.request.impl.CodeCompletionRequestImpl;
import com.github.isarthur.netbeans.editor.typingaid.ui.PopupUtil;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceInitializeHandler;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.Tree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaCodeFragmentCollectAndInsertHandler implements CodeFragmentCollectAndInsertHandler {

    private final JTextComponent component;
    private final Document document;

    public JavaCodeFragmentCollectAndInsertHandler(JTextComponent component) {
        this.component = component;
        this.document = component.getDocument();
    }

    @Override
    public List<CodeFragment> process(Abbreviation abbreviation) {
        List<CodeFragment> codeFragments = new ArrayList<>();
        JavaSource javaSource = JavaSourceInitializeHandler.getJavaSourceForDocument(document);
        AtomicReference<CodeCompletionRequest> atomicRequest = new AtomicReference<>();
        AtomicReference<CodeCompletionContext> atomicContext = new AtomicReference<>();
        try {
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                JavaSourceInitializeHandler.moveStateToResolvedPhase(copy);
                CodeCompletionRequest request =
                        new CodeCompletionRequestImpl(abbreviation, codeFragments, copy, component);
                atomicRequest.set(request);
                Tree.Kind currentTreeKind = JavaSourceUtilities.getCurrentTreeKind(request);
                CodeCompletionContext context = CodeCompletionContextFactory.getCodeCompletionContext(currentTreeKind);
                atomicContext.set(context);
                context.collect(request);
                int matchesCount = codeFragments.size();
                switch (matchesCount) {
                    case 0:
                        break;
                    case 1:
                        CodeFragment codeFragment = codeFragments.get(0);
                        context.insert(codeFragment, request);
                        break;
                    default:
                        codeFragments.removeIf(distinctByKey(CodeFragment::toString).negate());
                        switch (codeFragments.size()) {
                            case 0:
                                break;
                            case 1:
                                context.insert(codeFragments.get(0), request);
                                break;
                            default:
                                codeFragments.sort((fragment1, fragment2) ->
                                        fragment1.toString().compareTo(fragment2.toString()));
                                PopupUtil.showPopup(component, request);
                        }
                }
            });
            modificationResult.commit();
            if (codeFragments.size() == 1) {
                atomicContext.get().select(codeFragments.get(0), modificationResult, atomicRequest.get().getComponent());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(codeFragments);
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public Document getDocument() {
        return document;
    }
}
