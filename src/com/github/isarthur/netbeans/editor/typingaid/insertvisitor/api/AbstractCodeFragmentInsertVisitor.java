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

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.AssertKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.BreakKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CaseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CatchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ClassKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ContinueKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.DoKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ElseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.EnumKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.FinallyKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ForKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.IfKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ImportKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.InterfaceKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.NewKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ReturnKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.SwitchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.TryKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.VoidKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.WhileKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.api.Literal;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.FalseLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.NullLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.literal.impl.TrueLiteral;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.localelement.api.LocalElement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.api.MethodInvocation;
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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.api.PrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BooleanPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.BytePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.CharPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.DoublePrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.FloatPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.IntPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.LongPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.impl.ShortPrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.type.api.Type;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.AND;
import static com.sun.source.tree.Tree.Kind.AND_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.BITWISE_COMPLEMENT;
import static com.sun.source.tree.Tree.Kind.BLOCK;
import static com.sun.source.tree.Tree.Kind.CASE;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.CONDITIONAL_AND;
import static com.sun.source.tree.Tree.Kind.DIVIDE;
import static com.sun.source.tree.Tree.Kind.DIVIDE_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.DO_WHILE_LOOP;
import static com.sun.source.tree.Tree.Kind.ENHANCED_FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.EQUAL_TO;
import static com.sun.source.tree.Tree.Kind.FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.GREATER_THAN;
import static com.sun.source.tree.Tree.Kind.GREATER_THAN_EQUAL;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import static com.sun.source.tree.Tree.Kind.LEFT_SHIFT;
import static com.sun.source.tree.Tree.Kind.LEFT_SHIFT_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.LESS_THAN;
import static com.sun.source.tree.Tree.Kind.LESS_THAN_EQUAL;
import static com.sun.source.tree.Tree.Kind.LOGICAL_COMPLEMENT;
import static com.sun.source.tree.Tree.Kind.METHOD;
import static com.sun.source.tree.Tree.Kind.METHOD_INVOCATION;
import static com.sun.source.tree.Tree.Kind.MINUS;
import static com.sun.source.tree.Tree.Kind.MINUS_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.MULTIPLY;
import static com.sun.source.tree.Tree.Kind.MULTIPLY_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.NEW_CLASS;
import static com.sun.source.tree.Tree.Kind.NOT_EQUAL_TO;
import static com.sun.source.tree.Tree.Kind.PARENTHESIZED;
import static com.sun.source.tree.Tree.Kind.PLUS;
import static com.sun.source.tree.Tree.Kind.PLUS_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.PREFIX_DECREMENT;
import static com.sun.source.tree.Tree.Kind.PREFIX_INCREMENT;
import static com.sun.source.tree.Tree.Kind.REMAINDER;
import static com.sun.source.tree.Tree.Kind.REMAINDER_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.RETURN;
import static com.sun.source.tree.Tree.Kind.RIGHT_SHIFT;
import static com.sun.source.tree.Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.SWITCH;
import static com.sun.source.tree.Tree.Kind.UNSIGNED_RIGHT_SHIFT;
import static com.sun.source.tree.Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import static com.sun.source.tree.Tree.Kind.WHILE_LOOP;
import static com.sun.source.tree.Tree.Kind.XOR;
import static com.sun.source.tree.Tree.Kind.XOR_ASSIGNMENT;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import java.util.EnumSet;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TypeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

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
    public void visit(EnumKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(IfKeyword keyword, CodeCompletionRequest request) {
        insertTree(keyword, request);
    }

    @Override
    public void visit(ImportKeyword keyword, CodeCompletionRequest request) {
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
    public void visit(ThrowKeyword keyword, CodeCompletionRequest request) {
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
        Tree tree = getTreeToInsert(codeFragment, request);
        Tree newTree = getNewTree(codeFragment, tree, request);
        WorkingCopy copy = request.getWorkingCopy();
        copy.rewrite(originalTree, newTree);
    }

    private Tree getTreeToInsert(CodeFragment codeFragment, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeMaker make = copy.getTreeMaker();
        ExpressionTree initializer;
        Types types = copy.getTypes();
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.ABSTRACT));
            case ASSERT_KEYWORD:
                return make.Assert(make.Literal(true), make.Literal("")); //NOI18N
            case BOOLEAN_PRIMITIVE_TYPE:
            case BYTE_PRIMITIVE_TYPE:
            case CHAR_PRIMITIVE_TYPE:
            case DOUBLE_PRIMITIVE_TYPE:
            case FLOAT_PRIMITIVE_TYPE:
            case INT_PRIMITIVE_TYPE:
            case LONG_PRIMITIVE_TYPE:
            case SHORT_PRIMITIVE_TYPE:
                return getTreeForPrimitiveType(codeFragment, request);
            case BREAK_KEYWORD:
                if (!JavaSourceUtilities.getParentTreeOfKind(EnumSet.of(DO_WHILE_LOOP, ENHANCED_FOR_LOOP,
                        FOR_LOOP, SWITCH, WHILE_LOOP), request)) {
                    return null;
                }
                return make.Break(null);
            case CASE_KEYWORD:
                return make.Case(make.Identifier(""), Collections.singletonList(make.Break(null))); //NOI18N
            case CATCH_KEYWORD:
                return make.Catch(
                        make.Variable(
                                make.Modifiers(Collections.emptySet()),
                                "e", //NOI18N
                                make.Identifier("Exception"), //NOI18N
                                null),
                        make.Block(Collections.emptyList(), false));
            case CLASS_KEYWORD:
                return make.Class(
                        make.Modifiers(Collections.emptySet()),
                        "Class", //NOI18N
                        Collections.emptyList(),
                        null,
                        Collections.emptyList(),
                        Collections.emptyList());
            case CONTINUE_KEYWORD:
                if (!JavaSourceUtilities.getParentTreeOfKind(EnumSet.of(DO_WHILE_LOOP, ENHANCED_FOR_LOOP,
                        FOR_LOOP, WHILE_LOOP), request)) {
                    return null;
                }
                return make.Continue(null);
            case DO_KEYWORD:
                return make.DoWhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
            case ELSE_KEYWORD:
                IfTree originalTree = (IfTree) getOriginalTree(codeFragment, request);
                StatementTree elseTree = originalTree.getElseStatement();
                StatementTree newElseTree;
                if (elseTree == null) {
                    newElseTree = make.Block(Collections.emptyList(), false);
                } else {
                    newElseTree = make.If(
                            make.Parenthesized(make.Literal(true)),
                            make.Block(Collections.emptyList(), false),
                            elseTree);
                }
                return newElseTree;
            case ENUM_KEYWORD:
                return make.Enum(
                        make.Modifiers(Collections.emptySet()),
                        "Enum", //NOI18N
                        Collections.emptyList(),
                        Collections.emptyList());
            case EXTERNAL_TYPE:
            case GLOBAL_TYPE:
            case INTERNAL_TYPE:
                Type type = (Type) codeFragment;
                DeclaredType declaredType = types.getDeclaredType(type.getType());
                switch (request.getCurrentKind()) {
                    case BLOCK:
                        NewClassTree newClassTree = JavaSourceMaker.makeNewClassTree(type.getType(), request);
                        if (newClassTree != null) {
                            initializer = newClassTree;
                        } else {
                            initializer = make.Literal(null);
                        }
                        return make.Variable(make.Modifiers(Collections.emptySet()),
                                JavaSourceUtilities.getVariableName(declaredType, request),
                                make.Type(type.toString()),
                                initializer);
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree classTree = (ClassTree) getOriginalTree(codeFragment, request);
                        if (!JavaSourceUtilities.isMethodSection(classTree, request)) {
                            return make.Variable(make.Modifiers(Collections.singleton(Modifier.PRIVATE)),
                                    JavaSourceUtilities.getVariableName(declaredType, request),
                                    make.Type(type.toString()),
                                    null);
                        } else {
                            String returnVar = JavaSourceUtilities.returnVar(type.toString());
                            ReturnTree returnTree = make.Return(returnVar != null ? make.Identifier(returnVar) : null);
                            return make.Method(
                                    make.Modifiers(Collections.emptySet()),
                                    "method", //NOI18N
                                    make.QualIdent(type.toString()),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    make.Block(Collections.singletonList(returnTree), false),
                                    null);
                        }
                    case METHOD:
                        return make.Variable(make.Modifiers(Collections.emptySet()),
                                JavaSourceUtilities.getVariableName(declaredType, request),
                                make.Type(type.toString()),
                                null);
                    case PARAMETERIZED_TYPE:
                        return make.Type(type.toString());
                    case VARIABLE:
                        Abbreviation abbreviation = request.getAbbreviation();
                        TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
                        tokens.move(abbreviation.getStartOffset());
                        while (tokens.moveNext()) {
                            Token<?> token = tokens.token();
                            if (token.id() == JavaTokenId.WHITESPACE) {
                                continue;
                            }
                            if (token.id() == JavaTokenId.SEMICOLON) {
                                return JavaSourceMaker.makeNewClassTree(type.getType(), request);
                            } else {
                                VariableTree variableTree = (VariableTree) getOriginalTree(codeFragment, request);
                                return make.TypeCast(make.QualIdent(codeFragment.toString()), variableTree.getInitializer());
                            }
                        }
                        break;
                    default:
                        return JavaSourceMaker.makeNewClassTree(type.getType(), request);
                }
                return null;
            case FALSE_LITERAL:
            case NULL_LITERAL:
            case TRUE_LITERAL:
                Literal literal = (Literal) codeFragment;
                return make.Literal(literal.getIdentifier());
            case FINAL_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.FINAL));
            case FINALLY_KEYWORD:
                return make.Block(Collections.emptyList(), false);
            case FOR_KEYWORD:
                return make.ForLoop(
                        Collections.singletonList(make.Variable(
                                make.Modifiers(Collections.emptySet()),
                                "i", //NOI18N
                                make.PrimitiveType(TypeKind.INT),
                                make.Literal(0))),
                        make.Binary(Tree.Kind.LESS_THAN, make.Identifier("i"), make.Literal(10)), //NOI18N
                        Collections.singletonList(make.ExpressionStatement(
                                make.Unary(Tree.Kind.POSTFIX_INCREMENT, make.Identifier("i")))), //NOI18N
                        make.Block(Collections.emptyList(), false));
            case IF_KEYWORD:
                return make.If(make.Identifier("true"), make.Block(Collections.emptyList(), false), null);
            case IMPORT_KEYWORD:
                return make.Import(make.Identifier(""), false); //NOI18N
            case INTERFACE_KEYWORD:
                return make.Interface(
                        make.Modifiers(Collections.emptySet()),
                        "Interface", //NOI18N
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList());
            case LOCAL_ELEMENT:
                switch (request.getCurrentKind()) {
                    case AND:
                    case AND_ASSIGNMENT:
                    case ASSIGNMENT:
                    case BITWISE_COMPLEMENT:
                    case CONDITIONAL_AND:
                    case CONDITIONAL_OR:
                    case DIVIDE:
                    case DIVIDE_ASSIGNMENT:
                    case EQUAL_TO:
                    case GREATER_THAN:
                    case GREATER_THAN_EQUAL:
                    case LEFT_SHIFT:
                    case LEFT_SHIFT_ASSIGNMENT:
                    case LESS_THAN:
                    case LESS_THAN_EQUAL:
                    case LOGICAL_COMPLEMENT:
                    case METHOD_INVOCATION:
                    case MINUS:
                    case MINUS_ASSIGNMENT:
                    case MULTIPLY:
                    case MULTIPLY_ASSIGNMENT:
                    case NEW_CLASS:
                    case NOT_EQUAL_TO:
                    case PARENTHESIZED:
                    case PLUS:
                    case PLUS_ASSIGNMENT:
                    case PREFIX_DECREMENT:
                    case PREFIX_INCREMENT:
                    case REMAINDER:
                    case REMAINDER_ASSIGNMENT:
                    case RETURN:
                    case RIGHT_SHIFT:
                    case RIGHT_SHIFT_ASSIGNMENT:
                    case UNSIGNED_RIGHT_SHIFT:
                    case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                    case VARIABLE:
                    case XOR:
                    case XOR_ASSIGNMENT:
                        return make.Identifier(codeFragment.toString());
                    default:
                        LocalElement localElement = (LocalElement) codeFragment;
                        Element element = localElement.getIdentifier();
                        TypeMirror typeMirror = element.asType();
                        CharSequence typeName =
                                copy.getTypeUtilities().getTypeName(typeMirror, TypeUtilities.TypeNameOptions.PRINT_FQN);
                        String expression;
                        switch (typeName.toString()) {
                            case ConstantDataManager.BYTE:
                            case ConstantDataManager.SHORT:
                            case ConstantDataManager.INT:
                                expression = ConstantDataManager.ZERO;
                                break;
                            case ConstantDataManager.LONG:
                                expression = ConstantDataManager.ZERO_L;
                                break;
                            case ConstantDataManager.FLOAT:
                                expression = ConstantDataManager.ZERO_DOT_ZERO_F;
                                break;
                            case ConstantDataManager.DOUBLE:
                                expression = ConstantDataManager.ZERO_DOT_ZERO;
                                break;
                            case ConstantDataManager.CHAR:
                                expression = ConstantDataManager.EMPTY_CHAR;
                                break;
                            case ConstantDataManager.BOOLEAN:
                                expression = ConstantDataManager.TRUE;
                                break;
                            case ConstantDataManager.STRING:
                                expression = ConstantDataManager.EMPTY_STRING;
                                break;
                            default:
                                expression = ConstantDataManager.NULL;
                        }
                        AssignmentTree assignmentTree =
                                make.Assignment(make.Identifier(localElement.toString()), make.Identifier(expression));
                        return make.ExpressionStatement(assignmentTree);
                }
            case LOCAL_METHOD_INVOCATION:
            case NORMAL_METHOD_INVOCATION:
            case STATIC_METHOD_INVOCATION:
                MethodInvocation methodInvocation = (MethodInvocation) codeFragment;
                switch (request.getCurrentKind()) {
                    case BLOCK:
                    case CASE:
                    case SWITCH:
                        if (JavaSourceUtilities.isMethodReturnVoid(methodInvocation.getMethod())) {
                            return JavaSourceMaker.makeVoidMethodInvocationStatementTree(methodInvocation, request);
                        } else {
                            return JavaSourceMaker.makeMethodInvocationStatementTree(methodInvocation, request);
                        }
                    default:
                        return JavaSourceMaker.makeMethodInvocationExpressionTree(methodInvocation, request);
                }
            case NATIVE_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.NATIVE));
            case NEW_KEYWORD:
                TypeMirror typeInContext = JavaSourceUtilities.getTypeInContext(request);
                if (typeInContext == null) {
                    return null;
                }
                Element element = copy.getTypes().asElement(typeInContext);
                if (element == null) {
                    return null;
                }
                return JavaSourceMaker.makeNewClassTree((TypeElement) element, request);
            case PRIVATE_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.PRIVATE));
            case PROTECTED_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.PROTECTED));
            case PUBLIC_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.PUBLIC));
            case RETURN_KEYWORD:
                return JavaSourceUtilities.makeReturnTree(request);
            case STATIC_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.STATIC));
            case STRICTFP_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.STRICTFP));
            case SWITCH_KEYWORD:
                return make.Switch(
                        make.Identifier(""), //NOI18N
                        Collections.singletonList(
                                make.Case(make.Identifier(""), Collections.singletonList(make.Break(null))))); //NOI18N
            case SYNCHRONIZED_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.SYNCHRONIZED));
            case THROW_KEYWORD:
                return make.Throw(make.Identifier("new IllegalArgumentException()")); //NOI18N
            case TRANSIENT_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.TRANSIENT));
            case TRY_KEYWORD:
                return make.Try(
                        make.Block(Collections.emptyList(), false),
                        Collections.singletonList(
                                make.Catch(
                                        make.Variable(
                                                make.Modifiers(Collections.emptySet()),
                                                "e", //NOI18N
                                                make.Identifier("Exception"), //NOI18N
                                                null),
                                        make.Block(Collections.emptyList(), false))),
                        null);
            case VOID_KEYWORD:
                if (request.getCurrentKind() == CLASS || request.getCurrentKind() == ENUM) {
                    return make.Method(
                            make.Modifiers(Collections.emptySet()),
                            "method", //NOI18N
                            make.PrimitiveType(TypeKind.VOID),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            make.Block(Collections.emptyList(), false),
                            null);
                }
                return make.Identifier("void method();"); //NOI18N
            case VOLATILE_MODIFIER:
                return make.Modifiers(Collections.singleton(Modifier.VOLATILE));
            case WHILE_KEYWORD:
                return make.WhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
            default:
                return null;
        }
    }

    private Tree getTreeForPrimitiveType(CodeFragment codeFragment, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeMaker make = copy.getTreeMaker();
        Types types = copy.getTypes();
        PrimitiveType primitiveType = (PrimitiveType) codeFragment;
        TypeMirror type = types.getPrimitiveType(primitiveType.getTypeKind());
        switch (request.getCurrentKind()) {
            case BLOCK:
                LiteralTree initializer = make.Literal(primitiveType.getDefaultValue());
                return make.Variable(make.Modifiers(Collections.emptySet()),
                        JavaSourceUtilities.getVariableName(type, request),
                        make.Identifier(codeFragment.toString()),
                        initializer);
            case CLASS:
            case ENUM:
                ClassTree classTree = (ClassTree) getOriginalTree(codeFragment, request);
                if (!JavaSourceUtilities.isMethodSection(classTree, request)) {
                    return make.Variable(make.Modifiers(Collections.singleton(Modifier.PRIVATE)),
                            JavaSourceUtilities.getVariableName(type, request),
                            make.Identifier(codeFragment.toString()),
                            null);
                } else {
                    return make.Method(
                            make.Modifiers(Collections.emptySet()),
                            "method", //NOI18N
                            make.Identifier(codeFragment.toString()),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            make.Block(Collections.singletonList(
                                    make.Return(make.Literal(primitiveType.getDefaultValue()))), false),
                            null);
                }
            case METHOD:
                return make.Variable(make.Modifiers(Collections.emptySet()),
                        JavaSourceUtilities.getVariableName(type, request),
                        make.Identifier(codeFragment.toString()),
                        null);
            case VARIABLE:
                VariableTree variableTree = (VariableTree) request.getCurrentTree();
                return make.TypeCast(make.PrimitiveType(primitiveType.getTypeKind()), variableTree.getInitializer());
            default:
                return null;
        }
    }

    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        return request.getCurrentTree();
    }

    protected abstract Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request);
}
