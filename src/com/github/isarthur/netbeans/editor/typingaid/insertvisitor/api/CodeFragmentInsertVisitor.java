/*
 * Copyright 2020 Arthur Sadykov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {            }
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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.name.api.Name;
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

/**
 *
 * @author Arthur Sadykov
 */
public interface CodeFragmentInsertVisitor {

    default void visit(AbstractAbstractModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(AssertKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(BooleanPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(BreakKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(BytePrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(CaseKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(CatchKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ChainedFieldAccess fieldAccess, CodeCompletionRequest request) {
    }

    default void visit(ChainedMethodInvocation methodInvocation, CodeCompletionRequest request) {
    }

    default void visit(CharPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(ClassKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ContinueKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(DefaultKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(DoKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(DoublePrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(ElseKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(EnumKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ExtendsKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ExternalInnerType innerType, CodeCompletionRequest request) {
    }

    default void visit(FalseLiteral literal, CodeCompletionRequest request) {
    }

    default void visit(FinalModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(FinallyKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(FloatPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(ForKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(GlobalInnerType innerType, CodeCompletionRequest request) {
    }

    default void visit(IfKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ImplementsKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ImportKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(InstanceofKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(InterfaceKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(IntPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(LocalElement localElement, CodeCompletionRequest request) {
    }

    default void visit(LocalMethodInvocation methodInvocation, CodeCompletionRequest request) {
    }

    default void visit(LongPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(Name name, CodeCompletionRequest request) {
    }

    default void visit(NativeModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(NewKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(NormalMethodInvocation methodInvocation, CodeCompletionRequest request) {
    }

    default void visit(NullLiteral literal, CodeCompletionRequest request) {
    }

    default void visit(PrivateModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(ProtectedModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(PublicModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(ReturnKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ShortPrimitiveType primitiveType, CodeCompletionRequest request) {
    }

    default void visit(StaticFieldAccess fieldAccess, CodeCompletionRequest request) {
    }

    default void visit(StaticMethodInvocation methodInvocation, CodeCompletionRequest request) {
    }

    default void visit(StaticModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(StrictfpModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(SwitchKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(SynchronizedModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(ThisKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ThrowKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(ThrowsKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(TransientModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(TrueLiteral literal, CodeCompletionRequest request) {
    }

    default void visit(TryKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(Type type, CodeCompletionRequest request) {
    }

    default void visit(VoidKeyword keyword, CodeCompletionRequest request) {
    }

    default void visit(VolatileModifier modifier, CodeCompletionRequest request) {
    }

    default void visit(WhileKeyword keyword, CodeCompletionRequest request) {
    }
}
