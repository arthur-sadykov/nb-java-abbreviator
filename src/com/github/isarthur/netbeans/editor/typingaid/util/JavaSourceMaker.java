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
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.DO_WHILE_LOOP;
import static com.sun.source.tree.Tree.Kind.ENHANCED_FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.LESS_THAN;
import static com.sun.source.tree.Tree.Kind.POSTFIX_INCREMENT;
import static com.sun.source.tree.Tree.Kind.WHILE_LOOP;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
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
        WorkingCopy workingCopy = request.getWorkingCopy();
        return workingCopy.getTreeMaker();
    }

    public static AssertTree makeAssertTree(CodeCompletionRequest request) {
        LiteralTree condition = makeLiteralTree(true, request);
        tag(condition, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Assert(condition, makeLiteralTree("", request)); //NOI18N;
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
            List<? extends StatementTree> statements, boolean isStatic, CodeCompletionRequest request) {
        return getTreeMaker(request).Block(statements, isStatic);
    }

    public static BlockTree makeBlockTree(
            BlockTree blockTree, int insertIndex, StatementTree statementTree, CodeCompletionRequest request) {
        return getTreeMaker(request).insertBlockStatement(blockTree, insertIndex, statementTree);
    }

    public static BreakTree makeBreakTree(CodeCompletionRequest request) {
        return getTreeMaker(request).Break(null);
    }

    public static CaseTree makeCaseTree(CodeCompletionRequest request) {
        IdentifierTree expression = makeIdentifierTree("", request); //NOI18N
        tag(expression, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Case(expression, Collections.singletonList(makeBreakTree(request)));
    }

    public static CaseTree makeCaseTree(ExpressionTree expression, List<? extends StatementTree> statements,
            CodeCompletionRequest request) {
        return getTreeMaker(request).Case(expression, statements);
    }

    public static CaseTree makeCaseTree(CaseTree caseTree, int insertIndex, StatementTree statement,
            CodeCompletionRequest request) {
        return getTreeMaker(request).insertCaseStatement(caseTree, insertIndex, statement);
    }

    public static CatchTree makeCatchTree(CodeCompletionRequest request) {
        VariableTree variableTree =
                makeVariableTree(
                        makeModifiersTree(Collections.emptySet(), request),
                        "e", //NOI18N
                        makeIdentifierTree("Exception", request), //NOI18N
                        null,
                        request);
        tag(variableTree, ConstantDataManager.SECOND_IDENTIFIER_OR_LITERAL_TAG, request);
        return getTreeMaker(request).Catch(
                variableTree,
                makeBlockTree(Collections.emptyList(), false, request));
    }

    public static CatchTree makeCatchTree(VariableTree parameter, BlockTree block, CodeCompletionRequest request) {
        return getTreeMaker(request).Catch(parameter, block);
    }

    public static ClassTree makeClassTree(CodeCompletionRequest request) {
        ClassTree classTree = getTreeMaker(request).Class(
                makeModifiersTree(Collections.emptySet(), request),
                "Class", //NOI18N
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList());
        tag(classTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return classTree;
    }

    public static ClassTree makeClassEnumOrInterfaceTree(
            ClassTree classEnumOrInterface, int index, Tree member, CodeCompletionRequest request) {
        return getTreeMaker(request).insertClassMember(classEnumOrInterface, index, member);
    }

    public static ClassTree makeClassTree(ClassTree clazz, ExpressionTree extendz, CodeCompletionRequest request) {
        tag(extendz, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).setExtends(clazz, extendz);
    }

    public static ClassTree makeClassTree(ClassTree clazz, Tree implementz, CodeCompletionRequest request) {
        tag(implementz, ConstantDataManager.EXPRESSION_TAG, request);
        List<? extends Tree> implementsClause = clazz.getImplementsClause();
        WorkingCopy workingCopy = request.getWorkingCopy();
        TreeUtilities treeUtilities = workingCopy.getTreeUtilities();
        if (implementsClause.size() == 1) {
            if (treeUtilities.hasError(implementsClause.get(0))) {
                return getTreeMaker(request).Class(
                        clazz.getModifiers(),
                        clazz.getSimpleName(),
                        clazz.getTypeParameters(),
                        clazz.getExtendsClause(),
                        Collections.singletonList(implementz),
                        clazz.getMembers());
            } else {
                return getTreeMaker(request).addClassImplementsClause(clazz, implementz);
            }
        } else {
            return getTreeMaker(request).addClassImplementsClause(clazz, implementz);
        }
    }

    public static CompilationUnitTree makeCompilationUnitTree(
            CompilationUnitTree compilationUnit, int index, Tree typeDeclaration, CodeCompletionRequest request) {
        return getTreeMaker(request).insertCompUnitTypeDecl(compilationUnit, index, typeDeclaration);
    }

    public static CompilationUnitTree makeCompilationUnitTree(
            CompilationUnitTree compilationUnit, int index, ImportTree importt, CodeCompletionRequest request) {
        return getTreeMaker(request).insertCompUnitImport(compilationUnit, index, importt);
    }

    public static CompoundAssignmentTree makeCompoundAssignmentTree(
            Tree.Kind kind, ExpressionTree variableTree, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).CompoundAssignment(kind, variableTree, expressionTree);
    }

    public static ConditionalExpressionTree makeConditionalExpressionTree(
            ExpressionTree condition,
            ExpressionTree trueExpression,
            ExpressionTree falseExpression,
            CodeCompletionRequest request) {
        return getTreeMaker(request).ConditionalExpression(condition, trueExpression, falseExpression);
    }

    public static ContinueTree makeContinueTree(CodeCompletionRequest request) {
        if (!JavaSourceUtilities.getParentTreeOfKind(EnumSet.of(DO_WHILE_LOOP, ENHANCED_FOR_LOOP, FOR_LOOP, WHILE_LOOP),
                request)) {
            return null;
        }
        return getTreeMaker(request).Continue(null);
    }

    public static DoWhileLoopTree makeDoWhileLoopTree(CodeCompletionRequest request) {
        LiteralTree condition = makeLiteralTree(true, request);
        tag(condition, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).DoWhileLoop(condition, makeBlockTree(Collections.emptyList(), false, request));
    }

    public static StatementTree makeElseTree(CodeCompletionRequest request) {
        IfTree ifTree = (IfTree) request.getCurrentTree();
        StatementTree elseTree = ifTree.getElseStatement();
        if (elseTree == null) {
            return makeBlockTree(Collections.emptyList(), false, request);
        } else {
            LiteralTree condition = makeLiteralTree(true, request);
            tag(condition, ConstantDataManager.EXPRESSION_TAG, request);
            return makeIfTree(
                    makeParenthesizedTree(condition, request),
                    makeBlockTree(Collections.emptyList(), false, request),
                    elseTree,
                    request);
        }
    }

    public static ClassTree makeEnumTree(CodeCompletionRequest request) {
        ClassTree enumTree = getTreeMaker(request).Enum(
                makeModifiersTree(Collections.emptySet(), request),
                "Enum", //NOI18N
                Collections.emptyList(),
                Collections.emptyList());
        tag(enumTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return enumTree;
    }

    public static ExpressionStatementTree makeExpressionStatementTree(
            ExpressionTree expression, CodeCompletionRequest request) {
        return getTreeMaker(request).ExpressionStatement(expression);
    }

    public static ExpressionTree makeExtendsTree(String type, CodeCompletionRequest request) {
        return getTreeMaker(request).QualIdent(type);
    }

    public static ForLoopTree makeForLoopTree(CodeCompletionRequest request) {
        ForLoopTree forLoopTree =
                getTreeMaker(request).ForLoop(
                        Collections.singletonList(
                                makeVariableTree(
                                        makeModifiersTree(Collections.emptySet(), request),
                                        "i", //NOI18N,
                                        makePrimitiveTypeTree(TypeKind.INT, request),
                                        makeLiteralTree(0, request),
                                        request)),
                        makeBinaryTree(
                                LESS_THAN,
                                makeIdentifierTree("i", request),
                                makeLiteralTree(10, request),
                                request),
                        Collections.singletonList(
                                makeExpressionStatementTree(
                                        makeUnaryTree(POSTFIX_INCREMENT, makeIdentifierTree("i", request), request),
                                        request)),
                        makeBlockTree(Collections.emptyList(), false, request));
        tag(forLoopTree, ConstantDataManager.SECOND_INT_LITERAL_TAG, request);
        return forLoopTree;
    }

    public static IdentifierTree makeIdentifierTree(Element element, CodeCompletionRequest request) {
        return getTreeMaker(request).Identifier(element);
    }

    public static IdentifierTree makeIdentifierTree(String identifier, CodeCompletionRequest request) {
        return getTreeMaker(request).Identifier(identifier);
    }

    public static IfTree makeIfTree(CodeCompletionRequest request) {
        LiteralTree condition = makeLiteralTree(true, request);
        tag(condition, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).If(
                condition,
                makeBlockTree(Collections.emptyList(), false, request),
                null);
    }

    public static IfTree makeIfTree(ExpressionTree condition, StatementTree thenStatement, StatementTree elseStatement,
            CodeCompletionRequest request) {
        return getTreeMaker(request).If(condition, thenStatement, elseStatement);
    }

    public static ExpressionTree makeImplementsTree(String type, CodeCompletionRequest request) {
        return getTreeMaker(request).QualIdent(type);
    }

    public static ImportTree makeImportTree(CodeCompletionRequest request) {
        IdentifierTree qualifiedIdentifier = makeIdentifierTree("", request); //NOI18N
        tag(qualifiedIdentifier, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Import(qualifiedIdentifier, false);
    }

    public static ClassTree makeInterfaceTree(CodeCompletionRequest request) {
        ClassTree interfaceTree = getTreeMaker(request).Interface(
                makeModifiersTree(Collections.emptySet(), request),
                "Interface", //NOI18N
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        tag(interfaceTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return interfaceTree;
    }

    public static ClassTree makeInterfaceTree(ClassTree interfaze, ExpressionTree extendz, CodeCompletionRequest request) {
        tag(extendz, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Interface(
                interfaze.getModifiers(),
                interfaze.getSimpleName(),
                interfaze.getTypeParameters(),
                Collections.singletonList(extendz),
                interfaze.getMembers());
    }

    public static InstanceOfTree makeInstanceofTree(ExpressionTree expression, Tree type, CodeCompletionRequest request) {
        InstanceOfTree instanceOfTree = getTreeMaker(request).InstanceOf(expression, type);
        tag(type, ConstantDataManager.EXPRESSION_TAG, request);
        return instanceOfTree;
    }

    public static LiteralTree makeLiteralTree(Object literal, CodeCompletionRequest request) {
        return getTreeMaker(request).Literal(literal);
    }

    public static MethodTree makeMethodTree(String type, CodeCompletionRequest request) {
        String returnVar = JavaSourceUtilities.returnVar(type);
        ReturnTree returnTree = JavaSourceMaker.makeReturnTree(
                returnVar != null
                        ? JavaSourceMaker.makeIdentifierTree(returnVar, request)
                        : null,
                request);
        Tree methodType = JavaSourceMaker.makeTypeTree(type, request);
        MethodTree methodTree =
                getTreeMaker(request).Method(
                        makeModifiersTree(Collections.emptySet(), request),
                        "method", //NOI18N
                        methodType,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        JavaSourceMaker.makeBlockTree(Collections.singletonList(returnTree), false, request),
                        null);
        tag(methodTree, ConstantDataManager.METHOD_NAME_TAG, request);
        return methodTree;
    }

    public static MethodTree makeMethodTree(
            ModifiersTree modifiers,
            CharSequence name,
            Tree returnType,
            List<? extends TypeParameterTree> typeParameters,
            List<? extends VariableTree> parameters,
            List<? extends ExpressionTree> throwsList,
            BlockTree body,
            ExpressionTree defaultValue,
            CodeCompletionRequest request) {
        MethodTree methodTree = getTreeMaker(request).Method(
                modifiers, name, returnType, typeParameters, parameters, throwsList, body, defaultValue);
        tag(methodTree, ConstantDataManager.METHOD_NAME_TAG, request);
        return methodTree;
    }

    public static MethodTree makeMethodTree(
            MethodTree method, int index, VariableTree parameter, CodeCompletionRequest request) {
        TreeMaker treeMaker = getTreeMaker(request);
        if (!method.getParameters().isEmpty()) {
            MethodTree newMethod = treeMaker.removeMethodParameter(method, index);
            return treeMaker.insertMethodParameter(newMethod, index, parameter);
        }
        return treeMaker.insertMethodParameter(method, 0, parameter);
    }

    public static MethodTree makeMethodTree(MethodTree method, ExpressionTree throwz, CodeCompletionRequest request) {
        tag(throwz, ConstantDataManager.EXPRESSION_TAG, request);
        List<? extends ExpressionTree> throwsClause = method.getThrows();
        if (throwsClause.size() == 1) {
            if (throwsClause.get(0).toString().equals(ConstantDataManager.ANGLED_ERROR)) {
                return getTreeMaker(request).Method(
                        method.getModifiers(),
                        method.getName(),
                        method.getReturnType(),
                        method.getTypeParameters(),
                        method.getParameters(),
                        Collections.singletonList(throwz),
                        method.getBody(),
                        (ExpressionTree) method.getDefaultValue());
            } else {
                return getTreeMaker(request).addMethodThrows(method, throwz);
            }
        } else {
            return getTreeMaker(request).addMethodThrows(method, throwz);
        }
    }

    public static ExpressionTree makeMethodInvocationExpressionTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        TreeMaker make = getTreeMaker(request);
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod().resolve(request.getWorkingCopy())),
                        methodInvocation.getArguments());
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            tag(methodInvocationTree, ConstantDataManager.ARGUMENT_TAG, request);
            return methodInvocationTree;
        } else {
            MemberSelectTree memberSelectTree;
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                TypeElement scope =
                        ((StaticMethodInvocation) methodInvocation).getScope().resolve(request.getWorkingCopy());
                if (scope == null) {
                    throw new RuntimeException("Cannot resolve a type."); //NOI18N
                }
                memberSelectTree = make.MemberSelect(make.QualIdent(scope), methodInvocationTree.toString());
            } else {
                memberSelectTree =
                        make.MemberSelect(
                                make.Identifier(((NormalMethodInvocation) methodInvocation).getScope().toString()),
                                methodInvocationTree.toString());
            }
            tag(memberSelectTree, ConstantDataManager.ARGUMENT_TAG, request);
            return memberSelectTree;
        }
    }

    public static StatementTree makeMethodInvocationStatementTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeMaker make = copy.getTreeMaker();
        ModifiersTree modifiers = make.Modifiers(Collections.emptySet());
        ExecutableElement method = methodInvocation.getMethod().resolve(copy);
        if (method == null) {
            return null;
        }
        Types types = copy.getTypes();
        Tree type;
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(method),
                        methodInvocation.getArguments());
        ExpressionTree initializer;
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            initializer = methodInvocationTree;
            type = make.Type(method.getReturnType());
        } else {
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                TypeElement scope =
                        ((StaticMethodInvocation) methodInvocation).getScope().resolve(request.getWorkingCopy());
                if (scope == null) {
                    throw new RuntimeException("Cannot resolve a type."); //NOI18N
                }
                if (scope.getTypeParameters().isEmpty()) {
                    type = make.Type(method.getReturnType());
                } else {
                    TypeMirror scopeType = scope.asType();
                    TypeMirror variableTypeMirror =
                            types.asMemberOf((DeclaredType) scopeType, methodInvocation.getMethod().resolve(copy));
                    String variableType = variableTypeMirror.toString();
                    if (variableType.contains(")")) { //NOI18N
                        variableType = variableType.substring(variableType.indexOf(')') + 1).trim();
                    }
                    type = make.Type(variableType);
                }
                initializer = make.MemberSelect(make.QualIdent(scope), methodInvocationTree.toString());
            } else {
                Element scope = ((NormalMethodInvocation) methodInvocation).getScope();
                TypeMirror scopeType = scope.asType();
                TypeUtilities typeUtilities = copy.getTypeUtilities();
                CharSequence typeName =
                        typeUtilities.getTypeName(types.erasure(scopeType), TypeUtilities.TypeNameOptions.PRINT_FQN);
                Elements elements = copy.getElements();
                TypeElement typeElement = elements.getTypeElement(typeName);
                if (typeElement == null) {
                    return null;
                }
                if (typeElement.getTypeParameters().isEmpty()) {
                    type = make.Type(method.getReturnType());
                } else {
                    TypeMirror variableTypeMirror =
                            types.asMemberOf((DeclaredType) scopeType, methodInvocation.getMethod().resolve(copy));
                    String variableType = variableTypeMirror.toString();
                    if (variableType.contains(")")) { //NOI18N
                        variableType = variableType.substring(variableType.indexOf(')') + 1).trim();
                    }
                    type = make.Type(variableType);
                }
                initializer = make.MemberSelect(make.Identifier(scope), methodInvocationTree.toString());
            }
        }
        String variableName =
                JavaSourceUtilities.getVariableName(
                        method.getReturnType(),
                        request);
        return makeVariableTree(modifiers, variableName, type, initializer, request);
    }

    public static MethodInvocationTree makeMethodInvocationTree(
            MethodInvocationTree methodInvocation, int index, ExpressionTree argument, CodeCompletionRequest request) {
        TreeMaker treeMaker = getTreeMaker(request);
        if (!methodInvocation.getArguments().isEmpty()) {
            MethodInvocationTree newMethodInvocation = treeMaker.removeMethodInvocationArgument(methodInvocation, index);
            return treeMaker.insertMethodInvocationArgument(newMethodInvocation, index, argument);
        }
        return treeMaker.insertMethodInvocationArgument(methodInvocation, 0, argument);
    }

    public static ModifiersTree makeModifiersTree(Set<Modifier> modifiers, CodeCompletionRequest request) {
        return getTreeMaker(request).Modifiers(modifiers);
    }

    public static ModifiersTree makeModifiersTree(
            ModifiersTree modifiers, Modifier modifier, CodeCompletionRequest request) {
        return getTreeMaker(request).addModifiersModifier(modifiers, modifier);
    }

    public static ExpressionTree makeNewClassOrEnumAccessTree(TypeElement scope, TypeElement identifier,
            CodeCompletionRequest request) {
        if (identifier.getKind() == ElementKind.ENUM) {
            List<? extends Element> elements = identifier.getEnclosedElements();
            Element enumConstant = null;
            for (Element element : elements) {
                if (element.getKind() == ElementKind.ENUM_CONSTANT) {
                    enumConstant = element;
                    break;
                }
            }
            if (enumConstant == null) {
                return makeLiteralTree(null, request);
            }
            MemberSelectTree memberSelectTree = makeMemberSelectTree(
                    makeQualIdentTree(scope, request),
                    identifier,
                    request);
            return makeMemberSelectTree(memberSelectTree, enumConstant, request);
        }
        ExpressionTree newClassTree;
        if (identifier.getModifiers().contains(Modifier.STATIC)) {
            newClassTree = makeNewClassTree(identifier, request);
        } else {
            newClassTree = makeNewInnerClassTree(scope, identifier, request);
        }
        return newClassTree == null
                ? makeLiteralTree(null, request)
                : newClassTree;
    }

    public static NewClassTree makeNewClassTree(List<? extends ExpressionTree> typeArguments, ExpressionTree identifier,
            List<? extends ExpressionTree> arguments, ClassTree classBody, CodeCompletionRequest request) {
        return getTreeMaker(request).NewClass(null, typeArguments, identifier, arguments, classBody);
    }

    public static NewClassTree makeNewClassTree(
            NewClassTree newClass, int index, ExpressionTree typeArgument, CodeCompletionRequest request) {
        TreeMaker treeMaker = getTreeMaker(request);
        if (!newClass.getArguments().isEmpty()) {
            NewClassTree newNewClass = treeMaker.removeNewClassArgument(newClass, index);
            return treeMaker.insertNewClassArgument(newNewClass, index, typeArgument);
        }
        return treeMaker.insertNewClassArgument(newClass, 0, typeArgument);
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
                    make.QualIdent(type.getQualifiedName().toString()),
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
                make.QualIdent(type.getQualifiedName().toString()),
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

    public static MemberSelectTree makeNewInnerClassTree(
            TypeElement scope, TypeElement identifier, CodeCompletionRequest request) {
        NewClassTree newScopeClassTree = makeNewClassTree(scope, request);
        NewClassTree newIdentifierClassTree = makeNewClassTree(identifier, request);
        if (newScopeClassTree == null || newIdentifierClassTree == null) {
            return null;
        }
        String identifierTree = newIdentifierClassTree.toString();
        if (identifierTree.contains(".")) { //NOI18N
            identifierTree = "new " + identifierTree.substring(identifierTree.lastIndexOf('.') + 1); //NOI18N
        }
        return getTreeMaker(request).MemberSelect(newScopeClassTree, identifierTree);
    }

    public static MemberSelectTree makeMemberSelectTree(
            ExpressionTree expression, String identifier, CodeCompletionRequest request) {
        return getTreeMaker(request).MemberSelect(expression, identifier);
    }

    public static MemberSelectTree makeMemberSelectTree(
            ExpressionTree expression, Element element, CodeCompletionRequest request) {
        return getTreeMaker(request).MemberSelect(expression, element);
    }

    public static ParameterizedTypeTree makeParameterizedTypeTree(
            ParameterizedTypeTree parameterizedType, int index, ExpressionTree argument, CodeCompletionRequest request) {
        TreeMaker treeMaker = getTreeMaker(request);
        if (!parameterizedType.getTypeArguments().isEmpty()) {
            ParameterizedTypeTree newParameterizedType =
                    treeMaker.removeParameterizedTypeTypeArgument(parameterizedType, index);
            return treeMaker.insertParameterizedTypeTypeArgument(newParameterizedType, index, argument);
        }
        return treeMaker.insertParameterizedTypeTypeArgument(parameterizedType, 0, argument);
    }

    public static ParenthesizedTree makeParenthesizedTree(ExpressionTree expression, CodeCompletionRequest request) {
        return getTreeMaker(request).Parenthesized(expression);
    }

    public static PrimitiveTypeTree makePrimitiveTypeTree(TypeKind typeKind, CodeCompletionRequest request) {
        return getTreeMaker(request).PrimitiveType(typeKind);
    }

    public static ExpressionTree makeQualIdentTree(String name, CodeCompletionRequest request) {
        return getTreeMaker(request).QualIdent(name);
    }

    public static ExpressionTree makeQualIdentTree(Element element, CodeCompletionRequest request) {
        return getTreeMaker(request).QualIdent(element);
    }

    public static ReturnTree makeReturnTree(ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Return(expressionTree);
    }

    public static ReturnTree makeReturnTree(CodeCompletionRequest request) {
        String returnVar = JavaSourceUtilities.returnVar(request);
        TreeMaker make = getTreeMaker(request);
        if (returnVar == null) {
            return make.Return(null);
        }
        IdentifierTree identifier = make.Identifier(returnVar);
        tag(identifier, ConstantDataManager.EXPRESSION_TAG, request);
        ReturnTree returnTree = make.Return(identifier);
        return returnTree;
    }

    public static BlockTree makeStaticBlockTree(CodeCompletionRequest request) {
        return getTreeMaker(request).Block(Collections.emptyList(), true);
    }

    public static SwitchTree makeSwitchTree(CodeCompletionRequest request) {
        IdentifierTree expression = makeIdentifierTree("", request); //NOI18N
        tag(expression, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Switch(
                expression,
                Collections.singletonList(
                        makeCaseTree(
                                makeIdentifierTree("", request), //NOI18N
                                Collections.singletonList(makeBreakTree(request)),
                                request)));
    }

    public static SwitchTree makeSwitchTree(
            SwitchTree switchTree, int index, CaseTree caseTree, CodeCompletionRequest request) {
        return getTreeMaker(request).insertSwitchCase(switchTree, index, caseTree);
    }

    public static SynchronizedTree makeSynchronizedTree(CodeCompletionRequest request) {
        IdentifierTree expression = makeIdentifierTree("", request); //NOI18N
        tag(expression, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).Synchronized(
                expression,
                makeBlockTree(Collections.emptyList(), false, request));
    }

    public static ThrowTree makeThrowTree(CodeCompletionRequest request) {
        IdentifierTree identifier = makeIdentifierTree("IllegalArgumentException", request); //NOI18N
        tag(identifier, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return getTreeMaker(request).Throw(makeNewClassTree(
                Collections.emptyList(),
                identifier,
                Collections.emptyList(),
                null,
                request));
    }

    public static ThrowTree makeThrowTree(ExpressionTree expression, CodeCompletionRequest request) {
        tag(expression, ConstantDataManager.ARGUMENT_TAG, request);
        return getTreeMaker(request).Throw(expression);
    }

    public static ExpressionTree makeThrowsTree(String type, CodeCompletionRequest request) {
        ExpressionTree throwableType = getTreeMaker(request).QualIdent(type);
        tag(throwableType, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return throwableType;
    }

    public static TryTree makeTryTree(CodeCompletionRequest request) {
        return getTreeMaker(request).Try(
                makeBlockTree(Collections.emptyList(), false, request),
                Collections.singletonList(makeCatchTree(request)),
                null);
    }

    public static TryTree makeTryTree(TryTree tryTree, int index, CatchTree catchTree, CodeCompletionRequest request) {
        return getTreeMaker(request).insertTryCatch(tryTree, index, catchTree);
    }

    public static TryTree makeTryTree(
            BlockTree tryBlock, List<? extends CatchTree> catches, BlockTree finallyBlock, CodeCompletionRequest request) {
        return getTreeMaker(request).Try(tryBlock, catches, finallyBlock);
    }

    public static Tree makeTypeTree(String type, CodeCompletionRequest request) {
        return getTreeMaker(request).Type(type);
    }

    public static Tree makeTypeTree(TypeMirror type, CodeCompletionRequest request) {
        return getTreeMaker(request).Type(type);
    }

    public static TypeCastTree makeTypeCastTree(
            Tree type, ExpressionTree expressionTree, CodeCompletionRequest request) {
        TypeCastTree typeCastTree = getTreeMaker(request).TypeCast(type, expressionTree);
        tag(typeCastTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return typeCastTree;
    }

    public static UnaryTree makeUnaryTree(Tree.Kind kind, ExpressionTree expressionTree, CodeCompletionRequest request) {
        return getTreeMaker(request).Unary(kind, expressionTree);
    }

    public static VariableTree makeVariableTree(
            ModifiersTree modifiers, String name, Tree type, ExpressionTree initializer, CodeCompletionRequest request) {
        VariableTree variableTree = getTreeMaker(request).Variable(modifiers, name, type, initializer);
        tag(variableTree, ConstantDataManager.VARIABLE_NAME_TAG, request);
        return variableTree;
    }

    public static VariableTree makeVariableTree(
            VariableTree variableTree, ExpressionTree expressionTree, CodeCompletionRequest request) {
        VariableTree newVariableTree = getTreeMaker(request).Variable(
                variableTree.getModifiers(),
                variableTree.getName(),
                variableTree.getType(),
                expressionTree);
        tag(newVariableTree, ConstantDataManager.VARIABLE_NAME_TAG, request);
        return newVariableTree;
    }

    public static ExpressionStatementTree makeVoidMethodInvocationStatementTree(
            MethodInvocation methodInvocation, CodeCompletionRequest request) {
        TreeMaker make = getTreeMaker(request);
        MethodInvocationTree methodInvocationTree =
                make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod().resolve(request.getWorkingCopy())),
                        methodInvocation.getArguments());
        if (methodInvocation.getKind() == CodeFragment.Kind.CHAINED_METHOD_INVOCATION
                || methodInvocation.getKind() == CodeFragment.Kind.LOCAL_METHOD_INVOCATION) {
            ExpressionStatementTree expressionStatement = make.ExpressionStatement(methodInvocationTree);
            tag(expressionStatement, ConstantDataManager.ARGUMENT_TAG, request);
            return expressionStatement;
        } else {
            if (methodInvocation.getKind() == CodeFragment.Kind.STATIC_METHOD_INVOCATION) {
                TypeElement scope =
                        ((StaticMethodInvocation) methodInvocation).getScope().resolve(request.getWorkingCopy());
                if (scope == null) {
                    return null;
                }
                ExpressionStatementTree expressionStatement = make.ExpressionStatement(
                        make.MemberSelect(
                                make.QualIdent(scope),
                                methodInvocationTree.toString()));
                tag(expressionStatement, ConstantDataManager.ARGUMENT_TAG, request);
                return expressionStatement;
            } else {
                Element scope = ((NormalMethodInvocation) methodInvocation).getScope();
                ExpressionStatementTree expressionStatement =
                        make.ExpressionStatement(
                                make.MemberSelect(
                                        make.Identifier(scope),
                                        methodInvocationTree.toString()));
                tag(expressionStatement, ConstantDataManager.ARGUMENT_TAG, request);
                return expressionStatement;
            }
        }
    }

    public static Tree makeVoidTree(CodeCompletionRequest request) {
        if (request.getCurrentKind() == CLASS || request.getCurrentKind() == ENUM) {
            TreeMaker make = getTreeMaker(request);
            MethodTree methodTree =
                    make.Method(
                            make.Modifiers(Collections.emptySet()),
                            "method", //NOI18N
                            make.PrimitiveType(TypeKind.VOID),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            make.Block(Collections.emptyList(), false),
                            null);
            tag(methodTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
            return methodTree;
        }
        IdentifierTree identifierTree = makeIdentifierTree("void method();", request); //NOI18N
        tag(identifierTree, ConstantDataManager.FIRST_IDENTIFIER_OR_LITERAL_TAG, request);
        return identifierTree;
    }

    public static WhileLoopTree makeWhileLoopTree(CodeCompletionRequest request) {
        LiteralTree condition = makeLiteralTree(true, request);
        tag(condition, ConstantDataManager.EXPRESSION_TAG, request);
        return getTreeMaker(request).WhileLoop(
                condition,
                makeBlockTree(Collections.emptyList(), false, request));
    }

    private static void tag(Tree tree, String tag, CodeCompletionRequest request) {
        WorkingCopy workingCopy = request.getWorkingCopy();
        workingCopy.tag(tree, tag);
    }
}
