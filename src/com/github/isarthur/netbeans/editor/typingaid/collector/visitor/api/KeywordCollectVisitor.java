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
package com.github.isarthur.netbeans.editor.typingaid.collector.visitor.api;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.AssertKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.BreakKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CaseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CatchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ClassKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ContinueKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.DefaultKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.DoKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ElseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.EnumKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ExtendsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.FinallyKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ForKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.IfKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ImplementsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ImportKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.InstanceofKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.InterfaceKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.NewKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ReturnKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.SwitchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.SynchronizedKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThisKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.TryKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.VoidKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.WhileKeyword;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;

/**
 *
 * @author Arthur Sadykov
 */
public interface KeywordCollectVisitor {

    void visit(AssertKeyword keyword, CodeCompletionRequest request);

    void visit(BreakKeyword keyword, CodeCompletionRequest request);

    void visit(CaseKeyword keyword, CodeCompletionRequest request);

    void visit(CatchKeyword keyword, CodeCompletionRequest request);

    void visit(ClassKeyword keyword, CodeCompletionRequest request);

    void visit(ContinueKeyword keyword, CodeCompletionRequest request);

    void visit(DefaultKeyword keyword, CodeCompletionRequest request);

    void visit(DoKeyword keyword, CodeCompletionRequest request);

    void visit(ElseKeyword keyword, CodeCompletionRequest request);

    void visit(EnumKeyword keyword, CodeCompletionRequest request);

    void visit(ExtendsKeyword keyword, CodeCompletionRequest request);

    void visit(FinallyKeyword keyword, CodeCompletionRequest request);

    void visit(ForKeyword keyword, CodeCompletionRequest request);

    void visit(IfKeyword keyword, CodeCompletionRequest request);

    void visit(ImplementsKeyword keyword, CodeCompletionRequest request);

    void visit(ImportKeyword keyword, CodeCompletionRequest request);

    void visit(InstanceofKeyword keyword, CodeCompletionRequest request);

    void visit(InterfaceKeyword keyword, CodeCompletionRequest request);

    void visit(NewKeyword keyword, CodeCompletionRequest request);

    void visit(ReturnKeyword keyword, CodeCompletionRequest request);

    void visit(SwitchKeyword keyword, CodeCompletionRequest request);

    void visit(SynchronizedKeyword keyword, CodeCompletionRequest request);

    void visit(ThisKeyword keyword, CodeCompletionRequest request);

    void visit(ThrowKeyword keyword, CodeCompletionRequest request);

    void visit(ThrowsKeyword keyword, CodeCompletionRequest request);

    void visit(TryKeyword keyword, CodeCompletionRequest request);

    void visit(VoidKeyword keyword, CodeCompletionRequest request);

    void visit(WhileKeyword keyword, CodeCompletionRequest request);
}
