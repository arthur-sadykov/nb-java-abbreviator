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
package com.github.isarthur.netbeans.editor.typingaid.util;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.api.MethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.NormalMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.methodinvocation.impl.StaticMethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TypeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceMaker {

    private JavaSourceMaker() {
    }

    private static TreeMaker getTreeMaker(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        return copy.getTreeMaker();
    }

    public static AssignmentTree makeAssignmentTree(
            ExpressionTree variableTree, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Assignment(variableTree, expressionTree);
    }

    public static BinaryTree makeBinaryTree(
            Tree.Kind kind, ExpressionTree leftTree, ExpressionTree rightTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Binary(kind, leftTree, rightTree);
    }

    public static BlockTree makeBlockTree(
            BlockTree blockTree, int insertIndex, StatementTree statementTree, CodeCompletionRequest request) {
        return getTreeMaker(request).insertBlockStatement(blockTree, insertIndex, statementTree);
    }

    public static CompoundAssignmentTree makeCompoundAssignmentTree(
            Tree.Kind kind, ExpressionTree variableTree, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).CompoundAssignment(kind, variableTree, expressionTree);
    }

    public static IdentifierTree makeIdentifierTree(String identifier, CodeCompletionRequest request) {
        return getTreeMaker(request).Identifier(identifier);
    }

    public static ExpressionTree makeMethodInvocationExpressionTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        TreeMaker make = getTreeMaker(request);
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()),
                        methodInvocation.getArguments());
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            return methodInvocationTree;
        } else {
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                return make.MemberSelect(
                        make.QualIdent(((StaticMethodInvocation) methodInvocation).getScope().toString()),
                        methodInvocationTree.toString());
            } else {
                return make.MemberSelect(
                        make.Identifier(((NormalMethodInvocation) methodInvocation).getScope().toString()),
                        methodInvocationTree.toString());
            }
        }
    }

    public static StatementTree makeMethodInvocationStatementTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TypeUtilities typeUtilities = copy.getTypeUtilities();
        TreeMaker make = copy.getTreeMaker();
        ModifiersTree modifiers = make.Modifiers(Collections.emptySet());
        TypeMirror returnType = methodInvocation.getMethod().getReturnType();
        CharSequence returnTypeName = typeUtilities.getTypeName(returnType, TypeUtilities.TypeNameOptions.PRINT_FQN);
        Tree type = make.QualIdent(returnTypeName.toString());
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()),
                        methodInvocation.getArguments());
        ExpressionTree initializer;
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            initializer = methodInvocationTree;
        } else {
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                TypeElement scope = ((StaticMethodInvocation) methodInvocation).getScope();
                initializer = make.MemberSelect(make.QualIdent(scope), methodInvocationTree.toString());
            } else {
                Element scope = ((NormalMethodInvocation) methodInvocation).getScope();
                initializer = make.MemberSelect(make.Identifier(scope), methodInvocationTree.toString());
            }
        }
        String variableName =
                JavaSourceUtilities.getVariableName(
                        methodInvocation.getMethod().getReturnType(),
                        request);
        return make.Variable(modifiers, variableName, type, initializer);
    }

    public static NewClassTree makeNewClassTree(TypeElement type, CodeCompletionRequest request) {
        if (type.getKind() == ElementKind.INTERFACE
                || (type.getKind() == ElementKind.CLASS && type.getModifiers().contains(Modifier.ABSTRACT))) {
            return null;
        }
        WorkingCopy copy = request.getWorkingCopy();
        List<ExecutableElement> constructors = JavaSourceUtilities.getConstructors(type, copy);
        TreeMaker make = copy.getTreeMaker();
        if (constructors.isEmpty()) {
            return make.NewClass(
                    null,
                    Collections.emptyList(),
                    make.QualIdent(type.toString()),
                    Collections.emptyList(),
                    null);
        }
        constructors = JavaSourceUtilities.filterAccessibleConstructors(constructors, copy);
        if (constructors.isEmpty()) {
            return null;
        }
        ExecutableElement targetConstructor = JavaSourceUtilities.getTargetConstructor(constructors);
        NewClassTree newClassTree = make.NewClass(
                null,
                Collections.emptyList(),
                make.QualIdent(type),
                JavaSourceUtilities.evaluateMethodArguments(targetConstructor, request),
                null);
        if (type.getTypeParameters().isEmpty()) {
            return newClassTree;
        }
        Tree identifier = newClassTree.getIdentifier();
        Tree newIdentifier =
                make.ParameterizedType(make.Type(type.getQualifiedName().toString()), Collections.emptyList());
        copy.rewrite(identifier, newIdentifier);
        return newClassTree;
    }

    public static ExpressionTree makeQualIdentTree(Element element, CodeCompletionRequest request) {
        return getTreeMaker(request).QualIdent(element);
    }

    public static ReturnTree makeReturnTree(ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Return(expressionTree);
    }

    public static TypeCastTree makeTypeCastTree(
            Tree type, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).TypeCast(type, expressionTree);
    }

    public static UnaryTree makeUnaryTree(Tree.Kind kind, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Unary(kind, expressionTree);
    }

    public static VariableTree makeVariableTree(
            VariableTree variableTree, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Variable(
                variableTree.getModifiers(),
                variableTree.getName(),
                variableTree.getType(),
                expressionTree);
    }

    public static ExpressionStatementTree makeVoidMethodInvocationStatementTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        TreeMaker make = getTreeMaker(request);
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()),
                        methodInvocation.getArguments());
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            return make.ExpressionStatement(methodInvocationTree);
        } else {
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                TypeElement scope = ((StaticMethodInvocation) methodInvocation).getScope();
                return make.ExpressionStatement(
                        make.MemberSelect(
                                make.QualIdent(scope),
                                methodInvocationTree.toString()));
            } else {
                Element scope = ((NormalMethodInvocation) methodInvocation).getScope();
                return make.ExpressionStatement(
                        make.MemberSelect(
                                make.Identifier(scope),
                                methodInvocationTree.toString()));
            }
        }
    }
}
