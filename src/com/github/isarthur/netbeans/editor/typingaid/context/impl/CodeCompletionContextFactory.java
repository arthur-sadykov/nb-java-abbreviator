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
package com.github.isarthur.netbeans.editor.typingaid.context.impl;

import com.github.isarthur.netbeans.editor.typingaid.context.api.CodeCompletionContext;
import com.sun.source.tree.Tree;

/**
 *
 * @author Arthur Sadykov
 */
public class CodeCompletionContextFactory {

    private CodeCompletionContextFactory() {
    }

    public static CodeCompletionContext getCodeCompletionContext(Tree.Kind kind) {
        switch (kind) {
            case AND:
                return new AndCodeCompletionContext();
            case AND_ASSIGNMENT:
                return new AndAssignmentCodeCompletionContext();
            case ANNOTATED_TYPE:
                return new AnnotatedTypeCodeCompletionContext();
            case ANNOTATION:
                return new AnnotationCodeCompletionContext();
            case ANNOTATION_TYPE:
                return new AnnotationTypeCodeCompletionContext();
            case ARRAY_ACCESS:
                return new ArrayAccessCodeCompletionContext();
            case ARRAY_TYPE:
                return new ArrayTypeCodeCompletionContext();
            case ASSERT:
                return new AssertCodeCompletionContext();
            case ASSIGNMENT:
                return new AssignmentCodeCompletionContext();
            case BITWISE_COMPLEMENT:
                return new BitwiseComplementCodeCompletionContext();
            case BLOCK:
                return new BlockCodeCompletionContext();
            case BOOLEAN_LITERAL:
                return new BooleanLiteralCodeCompletionContext();
            case BREAK:
                return new BreakCodeCompletionContext();
            case CASE:
                return new CaseCodeCompletionContext();
            case CATCH:
                return new CatchCodeCompletionContext();
            case CHAR_LITERAL:
                return new CharLiteralCodeCompletionContext();
            case CLASS:
                return new ClassCodeCompletionContext();
            case COMPILATION_UNIT:
                return new CompilationUnitCodeCompletionContext();
            case CONDITIONAL_AND:
                return new ConditionalAndCodeCompletionContext();
            case CONDITIONAL_EXPRESSION:
                return new ConditionalExpressionCodeCompletionContext();
            case CONDITIONAL_OR:
                return new ConditionalOrCodeCompletionContext();
            case CONTINUE:
                return new ContinueCodeCompletionContext();
            case DIVIDE:
                return new DivideCodeCompletionContext();
            case DIVIDE_ASSIGNMENT:
                return new DivideAssignmentCodeCompletionContext();
            case DO_WHILE_LOOP:
                return new DoWhileLoopCodeCompletionContext();
            case DOUBLE_LITERAL:
                return new DoubleLiteralCodeCompletionContext();
            case EMPTY_STATEMENT:
                return new EmptyStatementCodeCompletionContext();
            case ENHANCED_FOR_LOOP:
                return new EnhancedForLoopCodeCompletionContext();
            case ENUM:
                return new EnumCodeCompletionContext();
            case EQUAL_TO:
                return new EqualToCodeCompletionContext();
            case ERRONEOUS:
                return new ErroneousCodeCompletionContext();
            case EXPRESSION_STATEMENT:
                return new ExpressionStatementCodeCompletionContext();
            case EXTENDS_WILDCARD:
                return new ExtendsWildcardCodeCompletionContext();
            case FLOAT_LITERAL:
                return new FloatLiteralCodeCompletionContext();
            case FOR_LOOP:
                return new ForLoopCodeCompletionContext();
            case GREATER_THAN:
                return new GreaterThanCodeCompletionContext();
            case GREATER_THAN_EQUAL:
                return new GreaterThanEqualCodeCompletionContext();
            case IDENTIFIER:
                return new IdentifierCodeCompletionContext();
            case IF:
                return new IfCodeCompletionContext();
            case IMPORT:
                return new ImportCodeCompletionContext();
            case INSTANCE_OF:
                return new InstanceofCodeCompletionContext();
            case INT_LITERAL:
                return new IntLiteralCodeCompletionContext();
            case INTERFACE:
                return new InterfaceCodeCompletionContext();
            case INTERSECTION_TYPE:
                return new IntersectionTypeCodeCompletionContext();
            case LABELED_STATEMENT:
                return new LabeledStatementCodeCompletionContext();
            case LAMBDA_EXPRESSION:
                return new LambdaExpressionCodeCompletionContext();
            case LEFT_SHIFT_ASSIGNMENT:
                return new LeftShiftAssignmentCodeCompletionContext();
            case LEFT_SHIFT:
                return new LeftShiftCodeCompletionContext();
            case LESS_THAN:
                return new LessThanCodeCompletionContext();
            case LESS_THAN_EQUAL:
                return new LessThanEqualCodeCompletionContext();
            case LOGICAL_COMPLEMENT:
                return new LogicalComplementCodeCompletionContext();
            case MEMBER_REFERENCE:
                return new MemberReferenceCodeCompletionContext();
            case MEMBER_SELECT:
                return new MemberSelectCodeCompletionContext();
            case METHOD:
                return new MethodCodeCompletionContext();
            case METHOD_INVOCATION:
                return new MethodInvocationCodeCompletionContext();
            case MINUS:
                return new MinusCodeCompletionContext();
            case MINUS_ASSIGNMENT:
                return new MinusAssignmentCompletionContext();
            case MODIFIERS:
                return new ModifiersCodeCompletionContext();
            case MULTIPLY:
                return new MultiplyCodeCompletionContext();
            case MULTIPLY_ASSIGNMENT:
                return new MultiplyAssignmentCodeCompletionContext();
            case NEW_ARRAY:
                return new NewArrayCodeCompletionContext();
            case NEW_CLASS:
                return new NewClassCodeCompletionContext();
            case NOT_EQUAL_TO:
                return new NotEqualToCodeCompletionContext();
            case NULL_LITERAL:
                return new NullLiteralCodeCompletionContext();
            case OR:
                return new OrCodeCompletionContext();
            case OR_ASSIGNMENT:
                return new OrAssignmentCodeCompletionContext();
            case OTHER:
                return new OtherCodeCompletionContext();
            case PARAMETERIZED_TYPE:
                return new ParameterizedTypeCodeCompletionContext();
            case PARENTHESIZED:
                return new ParenthesizedCodeCompletionContext();
            case PLUS:
                return new PlusCodeCompletionContext();
            case PLUS_ASSIGNMENT:
                return new PlusAssignmentCodeCompletionContext();
            case POSTFIX_DECREMENT:
                return new PostfixDecrementCodeCompletionContext();
            case POSTFIX_INCREMENT:
                return new PostfixIncrementCodeCompletionContext();
            case PREFIX_DECREMENT:
                return new PrefixDecrementCodeCompletionContext();
            case PREFIX_INCREMENT:
                return new PrefixIncrementCodeCompletionContext();
            case PRIMITIVE_TYPE:
                return new PrimitiveTypeCodeCompletionContext();
            case REMAINDER_ASSIGNMENT:
                return new RemainderAssignmentCodeCompletionContext();
            case REMAINDER:
                return new RemainderCodeCompletionContext();
            case RETURN:
                return new ReturnCodeCompletionContext();
            case RIGHT_SHIFT:
                return new RightShiftCodeCompletionContext();
            case RIGHT_SHIFT_ASSIGNMENT:
                return new RightShiftAssignmentCodeCompletionContext();
            case STRING_LITERAL:
                return new StringLiteralCodeCompletionContext();
            case SUPER_WILDCARD:
                return new SuperWildcardCodeCompletionContext();
            case SWITCH:
                return new SwitchCodeCompletionContext();
            case SYNCHRONIZED:
                return new SynchronizedCodeCompletionContext();
            case THROW:
                return new ThrowCodeCompletionContext();
            case TRY:
                return new TryCodeCompletionContext();
            case TYPE_ANNOTATION:
                return new TypeAnnotationCodeCompletionContext();
            case TYPE_CAST:
                return new TypeCastCodeCompletionContext();
            case TYPE_PARAMETER:
                return new TypeParameterCodeCompletionContext();
            case UNARY_MINUS:
                return new UnaryMinusCodeCompletionContext();
            case UNARY_PLUS:
                return new UnaryPlusCodeCompletionContext();
            case UNBOUNDED_WILDCARD:
                return new UnboundedWildcardCodeCompletionContext();
            case UNION_TYPE:
                return new UnionTypeCodeCompletionContext();
            case UNSIGNED_RIGHT_SHIFT:
                return new UnsignedRightShiftCodeCompletionContext();
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                return new UnsignedRightShiftAssignmentCodeCompletionContext();
            case VARIABLE:
                return new VariableCodeCompletionContext();
            case WHILE_LOOP:
                return new WhileLoopCodeCompletionContext();
            case XOR:
                return new XorCodeCompletionContext();
            case XOR_ASSIGNMENT:
                return new XorAssignmentCodeCompletionContext();
            default:
                return NullCodeCompletionContext.getInstance();
        }
    }
}
