/*
 * Copyright 2021 Arthur Sadykov.
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
package com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl.ChainedFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.fieldaccess.impl.StaticFieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.impl.ExternalInnerType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.innertype.impl.GlobalInnerType;
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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThisKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.TryKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.VoidKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.WhileKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.FalseLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.NullLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.TrueLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.localelement.api.LocalElement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.ChainedMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.LocalMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.NormalMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.StaticMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.AbstractAbstractModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.FinalModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.NativeModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.PrivateModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.ProtectedModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.PublicModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.StaticModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.StrictfpModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.SynchronizedModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.TransientModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.VolatileModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BooleanPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BytePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.CharPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.DoublePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.FloatPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.IntPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.LongPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.ShortPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.api.Type;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.Tree;
import org.netbeans.api.java.source.WorkingCopy;

public abstract class AbstractCodeFragmentInsertVisitor implements CodeFragmentInsertVisitor {

    @Override
    public void visit(AbstractAbstractModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(AssertKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(BooleanPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(BreakKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(BytePrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(CaseKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(CatchKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ChainedFieldAccess fieldAccess, CodeCompletionRequest request) {
        insertTree(fieldAccess, request);
    }

    @Override
    public void visit(ChainedMethodInvocation methodInvocation, CodeCompletionRequest request) {
        insertTree(methodInvocation, request);
    }

    @Override
    public void visit(CharPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(ClassKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ContinueKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(DefaultKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(DoKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(DoublePrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(ElseKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(EnumKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ExtendsKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ExternalInnerType innerType, CodeCompletionRequest request) {
        insertTree(innerType, request);
    }

    @Override
    public void visit(FalseLiteral literal, CodeCompletionRequest request) {
        insertTree(literal, request);
    }

    @Override
    public void visit(FinalModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(FinallyKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(FloatPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(ForKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(GlobalInnerType innerType, CodeCompletionRequest request) {
        insertTree(innerType, request);
    }

    @Override
    public void visit(IfKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ImplementsKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ImportKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(InstanceofKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(IntPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(InterfaceKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(LocalElement localElement, CodeCompletionRequest request) {
        insertTree(localElement, request);
    }

    @Override
    public void visit(LocalMethodInvocation methodInvocation, CodeCompletionRequest request) {
        insertTree(methodInvocation, request);
    }

    @Override
    public void visit(LongPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(NativeModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(NewKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(NormalMethodInvocation methodInvocation, CodeCompletionRequest request) {
        insertTree(methodInvocation, request);
    }

    @Override
    public void visit(NullLiteral literal, CodeCompletionRequest request) {
        insertTree(literal, request);
    }

    @Override
    public void visit(PrivateModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(ProtectedModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(PublicModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(ReturnKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ShortPrimitiveType primitiveType, CodeCompletionRequest request) {
        insertTree(primitiveType, request);
    }

    @Override
    public void visit(StaticFieldAccess fieldAccess, CodeCompletionRequest request) {
        insertTree(fieldAccess, request);
    }

    @Override
    public void visit(StaticMethodInvocation methodInvocation, CodeCompletionRequest request) {
        insertTree(methodInvocation, request);
    }

    @Override
    public void visit(StaticModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(StrictfpModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(SwitchKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(SynchronizedModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(ThisKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ThrowKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ThrowsKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(TransientModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    @Override
    public void visit(TrueLiteral literal, CodeCompletionRequest request) {
        insertTree(literal, request);
    }

    @Override
    public void visit(TryKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(Type type, CodeCompletionRequest request) {
        insertTree(type, request);
    }

    @Override
    public void visit(VoidKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(WhileKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(VolatileModifier modifier, CodeCompletionRequest request) {
        insertTree(modifier, request);
    }

    private void insertTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        Tree originalTree = getOriginalTree(codeFragment, request);
        Tree newTree = getNewTree(codeFragment, codeFragment.getTreeToInsert(request), request);
        WorkingCopy copy = request.getWorkingCopy();
        copy.rewrite(originalTree, newTree);
    }

    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        return request.getCurrentTree();
    }

    protected abstract Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request);
}
