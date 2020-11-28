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
package com.github.isarthur.netbeans.editor.typingaid;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.MethodCall;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.tree.Assert;
import com.github.isarthur.netbeans.editor.typingaid.tree.Assignment;
import com.github.isarthur.netbeans.editor.typingaid.tree.Block;
import com.github.isarthur.netbeans.editor.typingaid.tree.DoWhile;
import com.github.isarthur.netbeans.editor.typingaid.tree.EnhancedFor;
import com.github.isarthur.netbeans.editor.typingaid.tree.ExpressionStatement;
import com.github.isarthur.netbeans.editor.typingaid.tree.For;
import com.github.isarthur.netbeans.editor.typingaid.tree.If;
import com.github.isarthur.netbeans.editor.typingaid.tree.InsertableExpressionTree;
import com.github.isarthur.netbeans.editor.typingaid.tree.InsertableTree;
import com.github.isarthur.netbeans.editor.typingaid.tree.LambdaExpression;
import com.github.isarthur.netbeans.editor.typingaid.tree.MethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.tree.NewClass;
import com.github.isarthur.netbeans.editor.typingaid.tree.NullInsertableTree;
import com.github.isarthur.netbeans.editor.typingaid.tree.Parenthesized;
import com.github.isarthur.netbeans.editor.typingaid.tree.Return;
import com.github.isarthur.netbeans.editor.typingaid.tree.Switch;
import com.github.isarthur.netbeans.editor.typingaid.tree.Synchronized;
import com.github.isarthur.netbeans.editor.typingaid.tree.Variable;
import com.github.isarthur.netbeans.editor.typingaid.tree.While;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import static java.util.Objects.requireNonNull;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class TreeFactory {

    private TreeFactory() {
    }

    public static InsertableTree create(TreePath currentPath, MethodCall methodCall, WorkingCopy copy,
            JavaSourceHelper helper, int position) {
        requireNonNull(currentPath, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "currentPath")); //NOI18N
        requireNonNull(methodCall, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "methodCall")); //NOI18N
        requireNonNull(copy, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "copy")); //NOI18N
        requireNonNull(helper, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "helper")); //NOI18N
        Tree currentTree = currentPath.getLeaf();
        Tree.Kind kind = currentTree.getKind();
        switch (kind) {
            case AND:
            case AND_ASSIGNMENT:
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
            case MEMBER_SELECT:
            case MINUS:
            case MINUS_ASSIGNMENT:
            case MULTIPLY:
            case MULTIPLY_ASSIGNMENT:
            case NOT_EQUAL_TO:
            case OR:
            case OR_ASSIGNMENT:
            case PLUS:
            case PLUS_ASSIGNMENT:
            case REMAINDER:
            case REMAINDER_ASSIGNMENT:
            case RIGHT_SHIFT:
            case RIGHT_SHIFT_ASSIGNMENT:
            case UNARY_MINUS:
            case UNARY_PLUS:
            case UNSIGNED_RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            case XOR:
            case XOR_ASSIGNMENT:
                return new InsertableExpressionTree(currentPath, methodCall, copy, helper, position);
            case ASSERT:
                return new Assert(currentPath, methodCall, copy, helper, position);
            case ASSIGNMENT:
                return new Assignment(currentPath, methodCall, copy, helper, position);
            case BLOCK:
                return new Block(currentPath, methodCall, copy, helper, position);
            case DO_WHILE_LOOP:
                return new DoWhile(currentPath, methodCall, copy, helper, position);
            case ENHANCED_FOR_LOOP:
                return new EnhancedFor(currentPath, methodCall, copy, helper, position);
            case EXPRESSION_STATEMENT:
                return new ExpressionStatement(currentPath, methodCall, copy, helper, position);
            case FOR_LOOP:
                return new For(currentPath, methodCall, copy, helper, position);
            case IF:
                return new If(currentPath, methodCall, copy, helper, position);
            case LAMBDA_EXPRESSION:
                return new LambdaExpression(currentPath, methodCall, copy, helper, position);
            case METHOD_INVOCATION:
                return new MethodInvocation(currentPath, methodCall, copy, helper, position);
            case NEW_CLASS:
                return new NewClass(currentPath, methodCall, copy, helper, position);
            case PARENTHESIZED:
                return new Parenthesized(currentPath, methodCall, copy, helper, position);
            case RETURN:
                return new Return(currentPath, methodCall, copy, helper, position);
            case SYNCHRONIZED:
                return new Synchronized(currentPath, methodCall, copy, helper, position);
            case SWITCH:
                return new Switch(currentPath, methodCall, copy, helper, position);
            case VARIABLE:
                return new Variable(currentPath, methodCall, copy, helper, position);
            case WHILE_LOOP:
                return new While(currentPath, methodCall, copy, helper, position);
            default:
                return NullInsertableTree.getInstance(currentPath, methodCall, copy, helper, position);
        }
    }
}
