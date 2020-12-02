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

import com.github.isarthur.netbeans.editor.typingaid.util.Utilities;
import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.FieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Keyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.LocalElement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.MethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Name;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Statement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.spi.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CodeStyle;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.ui.ElementHeaders;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceHelper {

    private static final String TRUE = "true"; //NOI18N
    private static final String NULL = "null"; //NOI18N
    private static final String ZERO = "0"; //NOI18N
    private static final String ZERO_L = "0L"; //NOI18N
    private static final String ZERO_DOT_ZERO = "0.0"; //NOI18N
    private static final String ZERO_DOT_ZERO_F = "0.0F"; //NOI18N
    private static final String EMPTY_STRING = "\"\""; //NOI18N
    private static final String EMPTY_CHAR = "' '"; //NOI18N
    private static final String BYTE = "byte"; //NOI18N
    private static final String SHORT = "short"; //NOI18N
    private static final String INT = "int"; //NOI18N
    private static final String LONG = "long"; //NOI18N
    private static final String FLOAT = "float"; //NOI18N
    private static final String DOUBLE = "double"; //NOI18N
    private static final String CHAR = "char"; //NOI18N
    private static final String BOOLEAN = "boolean"; //NOI18N
    private static final String VOID = "void"; //NOI18N
    private static final String STRING = "java.lang.String"; //NOI18N
    private final JTextComponent component;
    private final Document document;
    private Abbreviation abbreviation;

    public JavaSourceHelper(JTextComponent component) {
        Parameters.notNull("component", component); //NOI18N
        this.component = component;
        this.document = component.getDocument();
    }

    void setAbbreviation(Abbreviation abbreviation) {
        this.abbreviation = abbreviation;
    }

    List<Element> getElementsByAbbreviation() {
        List<Element> localElements = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && getRequiredLocalElementKinds().contains(e.getKind());
                        });
                localMembersAndVars.forEach(localElements::add);
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        localElements.removeIf(element -> {
            String elementName = element.getSimpleName().toString();
            String elementAbbreviation = StringUtilities.getElementAbbreviation(elementName);
            return !elementAbbreviation.equals(abbreviation.getScope());
        });
        return Collections.unmodifiableList(localElements);
    }

    private JavaSource getJavaSourceForDocument(Document document) {
        JavaSource javaSource = JavaSource.forDocument(document);
        if (javaSource == null) {
            throw new IllegalStateException(ConstantDataManager.JAVA_SOURCE_NOT_ASSOCIATED_TO_DOCUMENT);
        }
        return javaSource;
    }

    private void moveStateToResolvedPhase(CompilationController controller) throws IOException {
        Phase phase = controller.toPhase(Phase.RESOLVED);
        if (phase.compareTo(Phase.RESOLVED) < 0) {
            throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_RESOLVED_PHASE);
        }
    }

    List<Name> collectVariableNames() {
        List<Name> names = new ArrayList<>();
        TypeMirror type = getTypeInContext();
        Set<String> variableNames = getVariableNames(type);
        variableNames.stream()
                .filter(name -> StringUtilities.getElementAbbreviation(name).equals(abbreviation.getName()))
                .forEach(name -> names.add(new Name(name)));
        return Collections.unmodifiableList(names);
    }

    private TypeMirror getTypeInContext() {
        AtomicReference<TypeMirror> typeMirror = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Trees trees = copy.getTrees();
                Types types = copy.getTypes();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                switch (currentTree.getKind()) {
                    case ASSIGNMENT: {
                        AssignmentTree assignmentTree = (AssignmentTree) currentTree;
                        ExpressionTree variable = assignmentTree.getVariable();
                        TreePath path = TreePath.getPath(currentPath, variable);
                        typeMirror.set(trees.getElement(path).asType());
                    }
                    case BLOCK:
                    case PARENTHESIZED: {
                        return;
                    }
                    case DIVIDE:
                    case EQUAL_TO:
                    case GREATER_THAN:
                    case GREATER_THAN_EQUAL:
                    case LESS_THAN:
                    case LESS_THAN_EQUAL:
                    case MINUS:
                    case MULTIPLY:
                    case NOT_EQUAL_TO:
                    case PLUS:
                    case REMAINDER: {
                        typeMirror.set(types.getPrimitiveType(TypeKind.DOUBLE));
                    }
                    case AND:
                    case AND_ASSIGNMENT:
                    case ARRAY_ACCESS:
                    case BITWISE_COMPLEMENT:
                    case LEFT_SHIFT:
                    case LEFT_SHIFT_ASSIGNMENT:
                    case OR:
                    case OR_ASSIGNMENT:
                    case POSTFIX_DECREMENT:
                    case POSTFIX_INCREMENT:
                    case PREFIX_DECREMENT:
                    case PREFIX_INCREMENT:
                    case RIGHT_SHIFT:
                    case RIGHT_SHIFT_ASSIGNMENT:
                    case UNSIGNED_RIGHT_SHIFT:
                    case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                    case XOR:
                    case XOR_ASSIGNMENT: {
                        typeMirror.set(types.getPrimitiveType(TypeKind.LONG));
                    }
                    case CONDITIONAL_AND:
                    case CONDITIONAL_OR:
                    case LOGICAL_COMPLEMENT: {
                        typeMirror.set(types.getPrimitiveType(TypeKind.BOOLEAN));
                    }
                    case MEMBER_SELECT: {
                        ExpressionTree expression = ((MemberSelectTree) currentTree).getExpression();
                        TreePath path = TreePath.getPath(currentPath, expression);
                        typeMirror.set(trees.getTypeMirror(path));
                    }
                    case METHOD_INVOCATION: {
                        int insertIndex = findIndexOfCurrentArgumentInMethod((MethodInvocationTree) currentTree);
                        Element element = trees.getElement(currentPath);
                        if (element.getKind() == ElementKind.METHOD) {
                            List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                            if (insertIndex != -1) {
                                VariableElement parameter = parameters.get(insertIndex);
                                typeMirror.set(parameter.asType());
                            }
                        }
                        break;
                    }
                    case VARIABLE: {
                        VariableTree variableTree = (VariableTree) currentTree;
                        Tree type = variableTree.getType();
                        TreePath path = TreePath.getPath(currentPath, type);
                        typeMirror.set(trees.getElement(path).asType());
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return typeMirror.get();
    }

    private int findIndexOfCurrentArgumentInMethod(MethodInvocationTree methodInvocationTree) {
        List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            ExpressionTree argument = arguments.get(i);
            if (argument.toString().equals(abbreviation.getContent())) {
                return i;
            }
        }
        return -1;
    }

    private Set<String> getVariableNames(TypeMirror typeMirror) {
        Set<String> names = new HashSet<>();
        try {
            List<Element> localElements = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && getRequiredLocalElementKinds().contains(e.getKind());
                        });
                localMembersAndVars.forEach(localElements::add);
                Iterator<String> nameSuggestions = Utilities.varNamesSuggestions(typeMirror, ElementKind.FIELD,
                        Collections.emptySet(), null, null, copy.getTypes(), copy.getElements(), localElements,
                        CodeStyle.getDefault(document)).iterator();
                while (nameSuggestions.hasNext()) {
                    names.add(nameSuggestions.next());
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableSet(names);
    }

    private Set<ElementKind> getRequiredLocalElementKinds() {
        Set<ElementKind> elementKinds = new HashSet<>(Byte.SIZE);
        if (Settings.getSettingForLocalVariable()) {
            elementKinds.add(ElementKind.LOCAL_VARIABLE);
        }
        if (Settings.getSettingForField()) {
            elementKinds.add(ElementKind.FIELD);
        }
        if (Settings.getSettingForParameter()) {
            elementKinds.add(ElementKind.PARAMETER);
        }
        if (Settings.getSettingForEnumConstant()) {
            elementKinds.add(ElementKind.ENUM_CONSTANT);
        }
        if (Settings.getSettingForExceptionParameter()) {
            elementKinds.add(ElementKind.EXCEPTION_PARAMETER);
        }
        if (Settings.getSettingForResourceVariable()) {
            elementKinds.add(ElementKind.RESOURCE_VARIABLE);
        }
        if (Settings.getSettingForInternalType()) {
            elementKinds.add(ElementKind.CLASS);
            elementKinds.add(ElementKind.INTERFACE);
            elementKinds.add(ElementKind.ENUM);
        }
        return Collections.unmodifiableSet(elementKinds);
    }

    List<TypeElement> collectTypesByAbbreviation() {
        JavaSource javaSource = getJavaSourceForDocument(document);
        ClasspathInfo classpathInfo = javaSource.getClasspathInfo();
        ClassIndex classIndex = classpathInfo.getClassIndex();
        Set<ElementHandle<TypeElement>> declaredTypes = classIndex.getDeclaredTypes(
                abbreviation.getScope().toUpperCase(),
                ClassIndex.NameKind.CAMEL_CASE,
                EnumSet.of(ClassIndex.SearchScope.SOURCE, ClassIndex.SearchScope.DEPENDENCIES));
        List<TypeElement> typeElements = new ArrayList<>();
        try {
            javaSource.runUserActionTask(compilationController -> {
                moveStateToResolvedPhase(compilationController);
                Elements elements = compilationController.getElements();
                declaredTypes.forEach(type -> {
                    TypeElement typeElement = type.resolve(compilationController);
                    if (typeElement != null) {
                        String typeName = typeElement.getSimpleName().toString();
                        String typeAbbreviation = StringUtilities.getElementAbbreviation(typeName);
                        if (typeAbbreviation.equals(abbreviation.getScope())) {
                            if (!elements.isDeprecated(typeElement)) {
                                typeElements.add(typeElement);
                            }
                        }
                    }
                });
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(typeElements);
    }

    List<MethodInvocation> collectMethodInvocations(List<Element> elements) {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getAllNonStaticMethodsInClassAndSuperclasses(element);
            methods = getMethodsByAbbreviation(methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodInvocations.add(new MethodInvocation(element, method, arguments, this));
            });
        });
        return Collections.unmodifiableList(methodInvocations);
    }

    private List<ExecutableElement> getAllNonStaticMethodsInClassAndSuperclasses(Element element) {
        List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(element);
        methods = filterNonStaticMethods(methods);
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> getAllMethodsInClassAndSuperclasses(Element element) {
        List<ExecutableElement> methods = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                TypeMirror typeMirror = element.asType();
                Iterable<? extends Element> members;
                try {
                    members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                        return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
                    });
                } catch (AssertionError error) {
                    return;
                }
                members.forEach(member -> methods.add((ExecutableElement) member));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> filterNonStaticMethods(List<ExecutableElement> methods) {
        return methods.stream()
                .filter(method -> (!method.getModifiers().contains(Modifier.STATIC)))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<CodeFragment> insertCodeFragment(MethodInvocation methodInvocation) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                Tree.Kind kind = currentTree.getKind();
                switch (kind) {
                    case AND:
                        insertAnd(methodInvocation);
                        break;
                    case AND_ASSIGNMENT:
                        insertAndAssignment(methodInvocation);
                        break;
                    case ASSIGNMENT:
                        insertAssignment(methodInvocation);
                        break;
                    case BLOCK:
                        insertBlockStatement(methodInvocation);
                        break;
                    case CONDITIONAL_AND:
                        insertConditionalAnd(methodInvocation);
                        break;
                    case CONDITIONAL_EXPRESSION:
                        insertConditionalStatement(methodInvocation);
                        break;
                    case CONDITIONAL_OR:
                        insertConditionalOr(methodInvocation);
                        break;
                    case DIVIDE:
                        insertDivide(methodInvocation);
                        break;
                    case DIVIDE_ASSIGNMENT:
                        insertDivideAssignment(methodInvocation);
                        break;
                    case EQUAL_TO:
                        insertEqualTo(methodInvocation);
                        break;
                    case FOR_LOOP:
                        insertForLoop(methodInvocation);
                        break;
                    case GREATER_THAN:
                        insertGreaterThan(methodInvocation);
                        break;
                    case GREATER_THAN_EQUAL:
                        insertGreaterThanEqual(methodInvocation);
                        break;
                    case LEFT_SHIFT:
                        insertLeftShift(methodInvocation);
                        break;
                    case LEFT_SHIFT_ASSIGNMENT:
                        insertLeftShiftAssignment(methodInvocation);
                        break;
                    case LESS_THAN:
                        insertLessThan(methodInvocation);
                        break;
                    case LESS_THAN_EQUAL:
                        insertLessThanEqual(methodInvocation);
                        break;
                    case LOGICAL_COMPLEMENT:
                        insertLogicalComplement(methodInvocation);
                        break;
                    case MEMBER_SELECT:
                        insertMemberSelect(methodInvocation);
                        break;
                    case METHOD_INVOCATION:
                        insertMethodInvocation(methodInvocation);
                        document.remove(component.getCaretPosition() - Short.BYTES, Short.BYTES);
                        break;
                    case MINUS:
                        insertMinus(methodInvocation);
                        break;
                    case MINUS_ASSIGNMENT:
                        insertMinusAssignment(methodInvocation);
                        break;
                    case MULTIPLY:
                        insertMultiply(methodInvocation);
                        break;
                    case MULTIPLY_ASSIGNMENT:
                        insertMultiplyAssignment(methodInvocation);
                        break;
                    case NEW_CLASS:
                        insertNewClass(methodInvocation);
                        document.remove(component.getCaretPosition() - Short.BYTES, Short.BYTES);
                        break;
                    case NOT_EQUAL_TO:
                        insertNotEqualTo(methodInvocation);
                        break;
                    case OR:
                        insertOr(methodInvocation);
                        break;
                    case OR_ASSIGNMENT:
                        insertOrAssignment(methodInvocation);
                        break;
                    case PARENTHESIZED:
                        insertParenthesized(methodInvocation);
                        break;
                    case PLUS:
                        insertPlus(methodInvocation);
                        break;
                    case PLUS_ASSIGNMENT:
                        insertPlusAssignment(methodInvocation);
                        break;
                    case REMAINDER:
                        insertRemainder(methodInvocation);
                        break;
                    case REMAINDER_ASSIGNMENT:
                        insertRemainderAssignment(methodInvocation);
                        break;
                    case RETURN:
                        insertReturnStatement(methodInvocation);
                        break;
                    case RIGHT_SHIFT:
                        insertRightShift(methodInvocation);
                        break;
                    case RIGHT_SHIFT_ASSIGNMENT:
                        insertRightShiftAssignment(methodInvocation);
                        break;
                    case UNARY_MINUS:
                        insertUnaryMinus(methodInvocation);
                        break;
                    case UNARY_PLUS:
                        insertUnaryPlus(methodInvocation);
                        break;
                    case UNSIGNED_RIGHT_SHIFT:
                        insertUnsignedRightShift(methodInvocation);
                        break;
                    case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                        insertUnsignedRightShiftAssignment(methodInvocation);
                        break;
                    case VARIABLE:
                        insertVariable(methodInvocation);
                        break;
                    case XOR:
                        insertXor(methodInvocation);
                        break;
                    case XOR_ASSIGNMENT:
                        insertXorAssignment(methodInvocation);
                        break;
                }
            });
            modificationResult.commit();
            return Collections.singletonList(methodInvocation);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<CodeFragment> insertChainedMethodInvocation(MethodInvocation methodInvocation, int position) {
        try {
            document.insertString(position, methodInvocation.toString(), null);
            return Collections.singletonList(methodInvocation);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    List<MethodInvocation> collectLocalMethodInvocations() {
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses();
        methods = getMethodsByAbbreviation(methods);
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            methodInvocations.add(new MethodInvocation(null, method, arguments, this));
        });
        return Collections.unmodifiableList(methodInvocations);
    }

    private List<ExecutableElement> getMethodsInCurrentAndSuperclasses() {
        JavaSource javaSource = getJavaSourceForDocument(document);
        List<ExecutableElement> methods = new ArrayList<>();
        try {
            javaSource.runUserActionTask(controller -> {
                moveStateToResolvedPhase(controller);
                Elements elements = controller.getElements();
                ElementUtilities elementUtilities = controller.getElementUtilities();
                TypeMirror typeMirror = getTypeMirrorOfCurrentClass();
                if (typeMirror == null) {
                    return;
                }
                Iterable<? extends Element> members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                    return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
                });
                methods.addAll(ElementFilter.methodsIn(members));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(methods);
    }

    private TypeMirror getTypeMirrorOfCurrentClass() {
        AtomicReference<TypeMirror> typeMirror = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                Trees trees = copy.getTrees();
                CompilationUnitTree compilationUnit = copy.getCompilationUnit();
                Tree tree = compilationUnit.getTypeDecls().get(0);
                if (tree.getKind() == Tree.Kind.CLASS) {
                    typeMirror.set(trees.getTypeMirror(TreePath.getPath(compilationUnit, tree)));
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return typeMirror.get();
    }

    private List<ExecutableElement> getMethodsByAbbreviation(List<ExecutableElement> methods) {
        List<ExecutableElement> result = new ArrayList<>();
        methods.forEach(method -> {
            String methodAbbreviation = StringUtilities.getMethodAbbreviation(method.getSimpleName().toString());
            if (methodAbbreviation.equals(abbreviation.getName())) {
                result.add(method);
            }
        });
        return Collections.unmodifiableList(result);
    }

    private List<ExpressionTree> evaluateMethodArguments(ExecutableElement method) {
        List<ExpressionTree> arguments = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                List<? extends VariableElement> parameters = method.getParameters();
                parameters.stream()
                        .map(parameter -> parameter.asType())
                        .forEachOrdered(elementType -> {
                            AtomicReference<IdentifierTree> identifierTree = new AtomicReference<>();
                            VariableElement variableElement = instanceOf(elementType.toString(), ""); //NOI18N
                            if (variableElement != null) {
                                identifierTree.set(make.Identifier(variableElement));
                                arguments.add(identifierTree.get());
                            } else {
                                switch (elementType.getKind()) {
                                    case BOOLEAN:
                                        identifierTree.set(make.Identifier(ConstantDataManager.FALSE));
                                        break;
                                    case BYTE:
                                    case SHORT:
                                    case INT:
                                        identifierTree.set(make.Identifier(ConstantDataManager.INTEGER_ZERO_LITERAL));
                                        break;
                                    case LONG:
                                        identifierTree.set(make.Identifier(ConstantDataManager.LONG_ZERO_LITERAL));
                                        break;
                                    case FLOAT:
                                        identifierTree.set(make.Identifier(ConstantDataManager.FLOAT_ZERO_LITERAL));
                                        break;
                                    case DOUBLE:
                                        identifierTree.set(make.Identifier(ConstantDataManager.DOUBLE_ZERO_LITERAL));
                                        break;
                                    default:
                                        identifierTree.set(make.Identifier(ConstantDataManager.NULL));
                                }
                                arguments.add(identifierTree.get());
                            }
                        });
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(arguments);
    }

    private VariableElement instanceOf(String typeName, String name) {
        AtomicReference<VariableElement> closest = new AtomicReference<>();
        try {
            List<Element> localElements = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Types types = copy.getTypes();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && getRequiredLocalElementKinds().contains(e.getKind());
                        });
                localMembersAndVars.forEach(localElements::add);
                TypeMirror type = type(typeName);
                if (type == null) {
                    return;
                }
                int distance = Integer.MAX_VALUE;
                for (Element element : localElements) {
                    if (VariableElement.class
                            .isInstance(element)
                            && !ConstantDataManager.ANGLED_ERROR.contentEquals(element.getSimpleName())
                            && element.asType().getKind() != TypeKind.ERROR
                            && types.isAssignable(element.asType(), type)) {
                        if (name.isEmpty()) {
                            closest.set((VariableElement) element);
                            return;
                        }
                        int d = ElementHeaders.getDistance(element.getSimpleName().toString()
                                .toLowerCase(), name.toLowerCase());
                        if (isSameType(element.asType(), type, types)) {
                            d -= 1000;
                        }
                        if (d < distance) {
                            distance = d;
                            closest.set((VariableElement) element);
                        }
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return closest.get();
    }

    private TypeMirror type(String typeName) {
        AtomicReference<TypeMirror> typeMirror = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Trees trees = copy.getTrees();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                String type = typeName.trim();
                if (type.isEmpty()) {
                    return;
                }
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                TypeElement enclosingClass = scope.getEnclosingClass();
                SourcePositions[] sourcePositions = new SourcePositions[1];
                StatementTree statement = treeUtilities.parseStatement("{" + type + " a;}", sourcePositions); //NOI18N
                if (statement.getKind() == Tree.Kind.BLOCK) {
                    List<? extends StatementTree> statements = ((BlockTree) statement).getStatements();
                    if (!statements.isEmpty()) {
                        StatementTree variable = statements.get(0);
                        if (variable.getKind() == Tree.Kind.VARIABLE) {
                            treeUtilities.attributeTree(statement, scope);
                            typeMirror.set(
                                    trees.getTypeMirror(new TreePath(currentPath, ((VariableTree) variable).getType())));
                        }
                    }
                }
                typeMirror.set(treeUtilities.parseType(type, enclosingClass));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return typeMirror.get();
    }

    private boolean isSameType(TypeMirror t1, TypeMirror t2, Types types) {
        if (types.isSameType(t1, t2)) {
            return true;
        }
        if (t1.getKind().isPrimitive() && types.isSameType(types.boxedClass((PrimitiveType) t1).asType(), t2)) {
            return true;
        }
        return t2.getKind().isPrimitive() && types.isSameType(t1, types.boxedClass((PrimitiveType) t1).asType());
    }

    JTextComponent getComponent() {
        return component;
    }

    Document getDocument() {
        return document;
    }

    List<MethodInvocation> collectStaticMethodInvocations() {
        List<TypeElement> typeElements = collectTypesByAbbreviation();
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        typeElements.forEach(element -> {
            List<ExecutableElement> methods = getStaticMethodsInClass(element);
            methods = getMethodsByAbbreviation(methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodInvocations.add(new MethodInvocation(element, method, arguments, this));
            });
        });
        return Collections.unmodifiableList(methodInvocations);
    }

    private List<ExecutableElement> getStaticMethodsInClass(TypeElement element) {
        Iterable<? extends Element> members;
        try {
            members = element.getEnclosedElements();
        } catch (AssertionError error) {
            return Collections.emptyList();
        }
        List<ExecutableElement> methods = ElementFilter.methodsIn(members);
        methods = filterStaticMethods(methods);
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> filterStaticMethods(List<ExecutableElement> methods) {
        List<ExecutableElement> staticMethods = new ArrayList<>();
        methods.stream().filter(method -> (method.getModifiers().contains(Modifier.STATIC)))
                .forEachOrdered(method -> {
                    staticMethods.add(method);
                });
        return Collections.unmodifiableList(staticMethods);
    }

    List<LocalElement> findLocalElements() {
        List<LocalElement> result = new ArrayList<>();
        try {
            List<Element> localElements = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && getRequiredLocalElementKinds().contains(e.getKind());
                        });
                localMembersAndVars.forEach(localElements::add);
                localElements
                        .stream()
                        .filter(element -> StringUtilities.getElementAbbreviation(
                                element.getSimpleName().toString()).equals(abbreviation.getName()))
                        .filter(distinctByKey(Element::getSimpleName))
                        .forEach(element -> result.add(new LocalElement(element)));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertLocalElement(LocalElement element) {
        try {
            document.insertString(abbreviation.getStartOffset(), element.toString(), null);
            return Collections.singletonList(element);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    List<Keyword> collectKeywords() {
        List<Keyword> keywords = new ArrayList<>();
        ConstantDataManager.KEYWORDS.forEach(keyword -> {
            String keywordAbbreviation = StringUtilities.getElementAbbreviation(keyword);
            if (keywordAbbreviation.equals(abbreviation.getName())) {
                keywords.add(new Keyword(keyword));
            }
        });
        Collections.sort(keywords, (keyword1, keyword2) -> {
            return keyword1.toString().compareTo(keyword2.toString());
        });
        return Collections.unmodifiableList(keywords);
    }

    public List<CodeFragment> insertKeyword(Keyword keyword) {
        try {
            document.insertString(abbreviation.getStartOffset(), keyword.toString(), null);
            return Collections.singletonList(keyword);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    boolean isMemberSelection() {
        AtomicBoolean memberSelection = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    memberSelection.set(false);
                } else {
                    memberSelection.set(currentPath.getLeaf().getKind() == Tree.Kind.MEMBER_SELECT);
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return memberSelection.get();
    }

    boolean isFieldOrParameterName() {
        AtomicBoolean memberSelection = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    memberSelection.set(false);
                } else {
                    memberSelection.set(currentPath.getLeaf().getKind() == Tree.Kind.VARIABLE);
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return memberSelection.get();
    }

    List<MethodInvocation> collectChainedMethodInvocations() {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                Types types = copy.getTypes();
                TypeMirror type = getTypeInContext();
                if (type == null) {
                    return;
                }
                Element typeElement = types.asElement(type);
                if (typeElement == null) {
                    return;
                }
                List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(typeElement);
                methods = getMethodsByAbbreviation(methods);
                methods.forEach(method -> {
                    List<ExpressionTree> arguments = evaluateMethodArguments(method);
                    methodInvocations.add(new MethodInvocation(null, method, arguments, this));
                });
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(methodInvocations);
    }

    List<FieldAccess> collectFieldAccesses() {
        List<TypeElement> typeElements = collectTypesByAbbreviation();
        List<FieldAccess> result = new ArrayList<>();
        typeElements.forEach(typeElement -> {
            try {
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                enclosedElements.stream().filter(element ->
                        ((element.getKind() == ElementKind.FIELD
                        && element.getModifiers().contains(Modifier.PUBLIC)
                        && element.getModifiers().contains(Modifier.STATIC)
                        && element.getModifiers().contains(Modifier.FINAL))
                        || element.getKind() == ElementKind.ENUM_CONSTANT)).forEachOrdered(element -> {
                    String elementName = element.getSimpleName().toString();
                    String elementAbbreviation = StringUtilities.getElementAbbreviation(elementName);
                    if (abbreviation.getName().equals(elementAbbreviation)) {
                        result.add(new FieldAccess(typeElement, element));
                    }
                });
            } catch (AssertionError ex) {
            }
        });
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertFieldAccess(FieldAccess fieldAccess) {
        List<CodeFragment> fieldAccesses = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                MemberSelectTree cs = make.MemberSelect(make.Identifier(fieldAccess.getScope()), fieldAccess.getName());
                try {
                    document.insertString(abbreviation.getStartOffset(), cs.toString(), null);
                    addImport(fieldAccess.getScope());
                    fieldAccesses.add(fieldAccess);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fieldAccesses);
    }

    List<Type> collectTypes() {
        List<TypeElement> types = collectTypesByAbbreviation();
        return types.stream()
                .filter(distinctByKey(element -> element.getSimpleName().toString()))
                .map(type -> new Type(type))
                .collect(Collectors.toUnmodifiableList());
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<CodeFragment> insertType(Type type) {
        try {
            document.insertString(abbreviation.getStartOffset(), type.toString(), null);
            return Collections.singletonList(type);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<CodeFragment> insertName(Name name) {
        try {
            document.insertString(abbreviation.getStartOffset(), name.toString(), null);
            return Collections.singletonList(name);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public void addImport(TypeElement type) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                CompilationUnitTree compilationUnit = copy.getCompilationUnit();
                TreeMaker make = copy.getTreeMaker();
                copy.rewrite(compilationUnit, make.addCompUnitImport(
                        compilationUnit,
                        make.Import(make.Identifier(type.getQualifiedName().toString()), false)));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    boolean afterThis() {
        AtomicBoolean afterThis = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TokenHierarchy<?> tokenHierarchy = copy.getTokenHierarchy();
                TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
                tokenSequence.move(abbreviation.getStartOffset());
                tokenSequence.movePrevious();
                tokenSequence.movePrevious();
                Token<?> token = tokenSequence.token();
                if (token != null) {
                    TokenId tokenId = token.id();
                    if (tokenId == JavaTokenId.THIS) {
                        afterThis.set(true);
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return afterThis.get();
    }

    List<LocalElement> collectFields() {
        List<LocalElement> result = new ArrayList<>();
        try {
            List<Element> fields = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && e.getKind() == ElementKind.FIELD;
                        });
                localMembersAndVars.forEach(fields::add);
                fields
                        .stream()
                        .filter(element -> StringUtilities.getElementAbbreviation(
                                element.getSimpleName().toString()).equals(abbreviation.getName()))
                        .filter(distinctByKey(Element::getSimpleName))
                        .forEach(element -> result.add(new LocalElement(element)));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertReturnStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        String returnVar = returnVar();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.BLOCK) {
                    return;
                }
                BlockTree currentBlock = (BlockTree) currentTree;
                int insertIndex = findInsertIndexInBlock(currentBlock);
                if (insertIndex == -1) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                ReturnTree returnStatement = make.Return(returnVar != null ? make.Identifier(returnVar) : null);
                BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, returnStatement);
                copy.rewrite(currentBlock, newBlock);
                statements.add(new Statement(returnStatement.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInBlock(BlockTree blockTree) {
        AtomicInteger insertIndex = new AtomicInteger(-1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                Trees trees = copy.getTrees();
                CompilationUnitTree compilationUnit = copy.getCompilationUnit();
                List<? extends StatementTree> statements = blockTree.getStatements();
                SourcePositions sourcePositions = trees.getSourcePositions();
                int size = statements.size();
                switch (size) {
                    case 0: {
                        insertIndex.set(0);
                        break;
                    }
                    case 1: {
                        StatementTree currentStatement = statements.get(0);
                        long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            insertIndex.set(0);
                            break;
                        } else {
                            insertIndex.set(1);
                            break;
                        }
                    }
                    case 2: {
                        StatementTree previousStatement = statements.get(0);
                        long previousStartPosition =
                                sourcePositions.getStartPosition(compilationUnit, previousStatement);
                        StatementTree currentStatement = statements.get(1);
                        long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                        if (abbreviation.getStartOffset() < previousStartPosition) {
                            insertIndex.set(0);
                            break;
                        } else if (currentStartPosition < abbreviation.getStartOffset()) {
                            insertIndex.set(size);
                            break;
                        } else {
                            insertIndex.set(1);
                            break;
                        }
                    }
                    default: {
                        for (int i = 1; i < size; i++) {
                            StatementTree previousStatement = statements.get(i - 1);
                            long previousStartPosition =
                                    sourcePositions.getStartPosition(compilationUnit, previousStatement);
                            StatementTree currentStatement = statements.get(i);
                            long currentStartPosition =
                                    sourcePositions.getStartPosition(compilationUnit, currentStatement);
                            if (i < size - 1) {
                                if (abbreviation.getStartOffset() < previousStartPosition) {
                                    insertIndex.set(i - 1);
                                    break;
                                } else if (previousStartPosition < abbreviation.getStartOffset()
                                        && abbreviation.getStartOffset() < currentStartPosition) {
                                    insertIndex.set(i);
                                    break;
                                }
                            } else {
                                if (abbreviation.getStartOffset() < currentStartPosition) {
                                    insertIndex.set(size - 1);
                                    break;
                                }
                                insertIndex.set(size);
                                break;
                            }
                        }
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return insertIndex.get();
    }

    private String returnVar() {
        String methodType = owningMethodType();
        if (methodType == null) {
            return null;
        }
        VariableElement variable = instanceOf(methodType, ""); //NOI18N
        if (variable != null) {
            return variable.getSimpleName().toString();
        } else {
            switch (methodType) {
                case BYTE:
                case SHORT:
                case INT:
                    return ZERO;
                case LONG:
                    return ZERO_L;
                case FLOAT:
                    return ZERO_DOT_ZERO_F;
                case DOUBLE:
                    return ZERO_DOT_ZERO;
                case CHAR:
                    return EMPTY_CHAR;
                case BOOLEAN:
                    return TRUE;
                case VOID:
                    return null;
                case STRING:
                    return EMPTY_STRING;
                default:
                    return NULL;
            }
        }
    }

    private String owningMethodType() {
        AtomicReference<String> methodType = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Trees trees = copy.getTrees();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                TreePath methodOrLambdaPath = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.LAMBDA_EXPRESSION, Tree.Kind.METHOD), currentPath);
                if (methodOrLambdaPath == null) {
                    return;
                }
                Tree methodOrLambda = methodOrLambdaPath.getLeaf();
                if (methodOrLambda.getKind() == Tree.Kind.METHOD) {
                    ExecutableElement method = (ExecutableElement) trees.getElement(methodOrLambdaPath);
                    TypeMirror returnType = method.getReturnType();
                    if (returnType != null) {
                        methodType.set(returnType.toString());
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return methodType.get();
    }

    public void insertReturnStatement(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.RETURN) {
                    return;
                }
                ReturnTree currentReturnTree = (ReturnTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                Tree newReturnTree = make.Return(methodInvocation);
                copy.rewrite(currentReturnTree, newReturnTree);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ExpressionTree createMethodInvocationWithoutReturnValue(MethodInvocation methodInvocation) {
        AtomicReference<ExpressionTree> expression = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()), methodInvocation.getArguments());
                if (methodInvocation.getScope() == null) {
                    expression.set(methodInvocationTree);
                } else {
                    expression.set(make.MemberSelect(make.Identifier(
                            methodInvocation.getScope()), methodInvocationTree.toString()));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return expression.get();
    }

    public void insertWhileStatement(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.WHILE_LOOP) {
                    return;
                }
                WhileLoopTree currentWhileTree = (WhileLoopTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                WhileLoopTree newWhileTree =
                        make.WhileLoop(
                                methodInvocation,
                                currentWhileTree.getStatement());
                copy.rewrite(currentWhileTree, newWhileTree);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertVariable(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.VARIABLE) {
                    return;
                }
                VariableTree currentVariable = (VariableTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                String initializer = currentVariable.getInitializer().toString();
                int errorIndex = initializer.indexOf("(ERROR)"); //NOI18N
                if (errorIndex >= 0) {
                    initializer = initializer.substring(0, errorIndex)
                            .concat(methodInvocation.toString())
                            .concat(initializer.substring(errorIndex + 7));
                }
                VariableTree newVariable =
                        make.Variable(
                                currentVariable.getModifiers(),
                                currentVariable.getName(),
                                currentVariable.getType(),
                                make.Identifier(initializer));
                copy.rewrite(currentVariable, newVariable);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertParenthesized(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.PARENTHESIZED) {
                    return;
                }
                ParenthesizedTree currentParenthesized = (ParenthesizedTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                ParenthesizedTree newParenthesized = make.Parenthesized(methodInvocation);
                copy.rewrite(currentParenthesized, newParenthesized);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertBlockStatement(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.BLOCK) {
                    return;
                }
                BlockTree currentBlock = (BlockTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                int insertIndex = findInsertIndexInBlock(currentBlock);
                if (insertIndex == -1) {
                    return;
                }
                BlockTree newBlock;
                MethodInvocation invocation = (MethodInvocation) fragment;
                if (isMethodReturnVoid(invocation.getMethod())) {
                    ExpressionStatementTree methodInvocation = createVoidMethodInvocation(invocation);
                    newBlock = make.insertBlockStatement(currentBlock, insertIndex, methodInvocation);
                } else {
                    VariableTree methodInvocation = createMethodInvocationWithReturnValue(invocation);
                    newBlock = make.insertBlockStatement(currentBlock, insertIndex, methodInvocation);
                }
                copy.rewrite(currentBlock, newBlock);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isMethodReturnVoid(ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        return returnType.getKind() == TypeKind.VOID;
    }

    public ExpressionStatementTree createVoidMethodInvocation(MethodInvocation methodInvocation) {
        AtomicReference<ExpressionStatementTree> expressionStatement = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()), methodInvocation.getArguments());
                if (methodInvocation.getScope() == null) {
                    expressionStatement.set(make.ExpressionStatement(methodInvocationTree));
                } else {
                    expressionStatement.set(make.ExpressionStatement(make.MemberSelect(
                            make.Identifier(methodInvocation.getScope()), methodInvocationTree.toString())));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return expressionStatement.get();
    }

    public VariableTree createMethodInvocationWithReturnValue(MethodInvocation methodInvocation) {
        AtomicReference<VariableTree> variable = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                ModifiersTree modifiers = make.Modifiers(Collections.emptySet());
                Tree type = make.Type(methodInvocation.getMethod().getReturnType());
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(
                        Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()),
                        methodInvocation.getArguments());
                ExpressionTree initializer;
                if (methodInvocation.getScope() == null) {
                    initializer = methodInvocationTree;
                } else {
                    if (TypeElement.class.isInstance(methodInvocation.getScope())) {
                        initializer =
                                make.MemberSelect(make.QualIdent(methodInvocation.getScope()), methodInvocationTree.toString());
                    } else {
                        initializer =
                                make.MemberSelect(make.Identifier(methodInvocation.getScope()), methodInvocationTree.toString());
                    }
                }
                Set<String> variableNames = getVariableNames(methodInvocation.getMethod().getReturnType());
                String variableName = variableNames.isEmpty() ? "" : variableNames.iterator().next(); //NOI18N
                variable.set(make.Variable(modifiers, variableName, type, initializer));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return variable.get();
    }

    public void insertAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.ASSIGNMENT) {
                    return;
                }
                AssignmentTree currentAssignment = (AssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                AssignmentTree newAssignment = make.Assignment(currentAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentAssignment, newAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertConditionalOr(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.CONDITIONAL_OR) {
                    return;
                }
                BinaryTree currentConditionalOr = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newConditionalOr = make.Binary(
                        Tree.Kind.CONDITIONAL_OR, currentConditionalOr.getLeftOperand(), methodInvocation);
                copy.rewrite(currentConditionalOr, newConditionalOr);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertAnd(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.AND) {
                    return;
                }
                BinaryTree currentAnd = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newAnd = make.Binary(Tree.Kind.AND, currentAnd.getLeftOperand(), methodInvocation);
                copy.rewrite(currentAnd, newAnd);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertAndAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.AND_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentAndAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newAndAssignment =
                        make.CompoundAssignment(Tree.Kind.AND, currentAndAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentAndAssignment, newAndAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertConditionalAnd(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.CONDITIONAL_AND) {
                    return;
                }
                BinaryTree currentConditionalAnd = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newConditionalAnd = make.Binary(
                        Tree.Kind.CONDITIONAL_AND, currentConditionalAnd.getLeftOperand(), methodInvocation);
                copy.rewrite(currentConditionalAnd, newConditionalAnd);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertDivide(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.DIVIDE) {
                    return;
                }
                BinaryTree currentDivide = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newDivide = make.Binary(
                        Tree.Kind.DIVIDE, currentDivide.getLeftOperand(), methodInvocation);
                copy.rewrite(currentDivide, newDivide);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertDivideAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.DIVIDE_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentDivideAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newDivideAssignment = make.CompoundAssignment(
                        Tree.Kind.DIVIDE_ASSIGNMENT, currentDivideAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentDivideAssignment, newDivideAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertEqualTo(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.EQUAL_TO) {
                    return;
                }
                BinaryTree currentEqualTo = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newEqualTo =
                        make.Binary(Tree.Kind.EQUAL_TO, currentEqualTo.getLeftOperand(), methodInvocation);
                copy.rewrite(currentEqualTo, newEqualTo);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertGreaterThan(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.GREATER_THAN) {
                    return;
                }
                BinaryTree currentGreaterThan = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newGreaterThan =
                        make.Binary(Tree.Kind.GREATER_THAN, currentGreaterThan.getLeftOperand(), methodInvocation);
                copy.rewrite(currentGreaterThan, newGreaterThan);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertGreaterThanEqual(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.GREATER_THAN_EQUAL) {
                    return;
                }
                BinaryTree currentGreaterThanEqual = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newGreaterThanEqual = make.Binary(
                        Tree.Kind.GREATER_THAN_EQUAL, currentGreaterThanEqual.getLeftOperand(), methodInvocation);
                copy.rewrite(currentGreaterThanEqual, newGreaterThanEqual);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertLeftShift(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.LEFT_SHIFT) {
                    return;
                }
                BinaryTree currentLeftShift = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newLeftShift = make.Binary(
                        Tree.Kind.LEFT_SHIFT, currentLeftShift.getLeftOperand(), methodInvocation);
                copy.rewrite(currentLeftShift, newLeftShift);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertLeftShiftAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.LEFT_SHIFT_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentLeftShiftAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newLeftShiftAssignment = make.CompoundAssignment(
                        Tree.Kind.LEFT_SHIFT_ASSIGNMENT, currentLeftShiftAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentLeftShiftAssignment, newLeftShiftAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertLessThan(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.LESS_THAN) {
                    return;
                }
                BinaryTree currentLessThan = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newLessThan = make.Binary(
                        Tree.Kind.LESS_THAN, currentLessThan.getLeftOperand(), methodInvocation);
                copy.rewrite(currentLessThan, newLessThan);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertLessThanEqual(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.LESS_THAN_EQUAL) {
                    return;
                }
                BinaryTree currentLessThanEqual = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newLessThanEqual = make.Binary(
                        Tree.Kind.LESS_THAN_EQUAL, currentLessThanEqual.getLeftOperand(), methodInvocation);
                copy.rewrite(currentLessThanEqual, newLessThanEqual);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertLogicalComplement(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.LOGICAL_COMPLEMENT) {
                    return;
                }
                UnaryTree currentLogicalComplement = (UnaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                UnaryTree newLogicalComplement = make.Unary(Tree.Kind.LOGICAL_COMPLEMENT, methodInvocation);
                copy.rewrite(currentLogicalComplement, newLogicalComplement);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMemberSelect(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.MEMBER_SELECT) {
                    return;
                }
                MemberSelectTree currentMemberSelect = (MemberSelectTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                MemberSelectTree newMemberSelect =
                        make.MemberSelect(currentMemberSelect.getExpression(), methodInvocation.toString());
                copy.rewrite(currentMemberSelect, newMemberSelect);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMinus(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.MINUS) {
                    return;
                }
                BinaryTree currentMinus = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newMinus = make.Binary(Tree.Kind.MINUS, currentMinus.getLeftOperand(), methodInvocation);
                copy.rewrite(currentMinus, newMinus);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMinusAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.MINUS_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentMinusAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newMinusAssignment = make.CompoundAssignment(
                        Tree.Kind.MINUS_ASSIGNMENT, currentMinusAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentMinusAssignment, newMinusAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMultiply(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.MULTIPLY) {
                    return;
                }
                BinaryTree currentMultiply = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newMultiply =
                        make.Binary(Tree.Kind.MULTIPLY, currentMultiply.getLeftOperand(), methodInvocation);
                copy.rewrite(currentMultiply, newMultiply);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMultiplyAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.MULTIPLY_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentMultiplyAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newMultiplyAssignment = make.CompoundAssignment(
                        Tree.Kind.MULTIPLY_ASSIGNMENT, currentMultiplyAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentMultiplyAssignment, newMultiplyAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertNotEqualTo(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.NOT_EQUAL_TO) {
                    return;
                }
                BinaryTree currentNotEqualTo = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newNotEqualTo =
                        make.Binary(Tree.Kind.NOT_EQUAL_TO, currentNotEqualTo.getLeftOperand(), methodInvocation);
                copy.rewrite(currentNotEqualTo, newNotEqualTo);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertOr(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.OR) {
                    return;
                }
                BinaryTree currentOr = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newOr = make.Binary(Tree.Kind.OR, currentOr.getLeftOperand(), methodInvocation);
                copy.rewrite(currentOr, newOr);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertOrAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.OR_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentOrAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newOrAssignment = make.CompoundAssignment(
                        Tree.Kind.OR_ASSIGNMENT, currentOrAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentOrAssignment, newOrAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertPlus(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.PLUS) {
                    return;
                }
                BinaryTree currentPlus = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newPlus = make.Binary(Tree.Kind.PLUS, currentPlus.getLeftOperand(), methodInvocation);
                copy.rewrite(currentPlus, newPlus);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertPlusAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.PLUS_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentPlusAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newPlusAssignment = make.CompoundAssignment(
                        Tree.Kind.PLUS_ASSIGNMENT, currentPlusAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentPlusAssignment, newPlusAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertRemainder(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.REMAINDER) {
                    return;
                }
                BinaryTree currentRemainder = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newRemainder =
                        make.Binary(Tree.Kind.REMAINDER, currentRemainder.getLeftOperand(), methodInvocation);
                copy.rewrite(currentRemainder, newRemainder);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertRemainderAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.REMAINDER_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentRemainderAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newRemainderAssignment = make.CompoundAssignment(
                        Tree.Kind.REMAINDER_ASSIGNMENT, currentRemainderAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentRemainderAssignment, newRemainderAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertRightShift(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.RIGHT_SHIFT) {
                    return;
                }
                BinaryTree currentRightShift = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newRightShift =
                        make.Binary(Tree.Kind.RIGHT_SHIFT, currentRightShift.getLeftOperand(), methodInvocation);
                copy.rewrite(currentRightShift, newRightShift);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertRightShiftAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.RIGHT_SHIFT_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentRightShiftAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newRightShiftAssignment = make.CompoundAssignment(
                        Tree.Kind.RIGHT_SHIFT_ASSIGNMENT, currentRightShiftAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentRightShiftAssignment, newRightShiftAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertUnaryMinus(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.UNARY_MINUS) {
                    return;
                }
                UnaryTree currentUnaryMinus = (UnaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                UnaryTree newUnaryMinus = make.Unary(Tree.Kind.UNARY_MINUS, methodInvocation);
                copy.rewrite(currentUnaryMinus, newUnaryMinus);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertUnaryPlus(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.UNARY_PLUS) {
                    return;
                }
                UnaryTree currentUnaryPlus = (UnaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                UnaryTree newUnaryPlus = make.Unary(Tree.Kind.UNARY_PLUS, methodInvocation);
                copy.rewrite(currentUnaryPlus, newUnaryPlus);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertUnsignedRightShift(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.UNSIGNED_RIGHT_SHIFT) {
                    return;
                }
                BinaryTree currentUnsignedRightShift = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newUnsignedRightShift = make.Binary(
                        Tree.Kind.UNSIGNED_RIGHT_SHIFT, currentUnsignedRightShift.getLeftOperand(), methodInvocation);
                copy.rewrite(currentUnsignedRightShift, newUnsignedRightShift);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertUnsignedRightShiftAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentUnsignedRightShiftAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newUnsignedRightShiftAssignment = make.CompoundAssignment(
                        Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT,
                        currentUnsignedRightShiftAssignment.getVariable(),
                        methodInvocation);
                copy.rewrite(currentUnsignedRightShiftAssignment, newUnsignedRightShiftAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertXor(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.XOR) {
                    return;
                }
                BinaryTree currentXor = (BinaryTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                BinaryTree newXor = make.Binary(Tree.Kind.XOR, currentXor.getLeftOperand(), methodInvocation);
                copy.rewrite(currentXor, newXor);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertXorAssignment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.XOR_ASSIGNMENT) {
                    return;
                }
                CompoundAssignmentTree currentXorAssignment = (CompoundAssignmentTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                CompoundAssignmentTree newXorAssignment = make.CompoundAssignment(
                        Tree.Kind.XOR, currentXorAssignment.getVariable(), methodInvocation);
                copy.rewrite(currentXorAssignment, newXorAssignment);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertForLoop(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.FOR_LOOP) {
                    return;
                }
                ForLoopTree currentForLoop = (ForLoopTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
                sequence.move(abbreviation.getStartOffset());
                int semicolonCount = 0;
                while (sequence.movePrevious() && sequence.token().id() != JavaTokenId.FOR) {
                    TokenId tokenId = sequence.token().id();
                    if (tokenId == JavaTokenId.SEMICOLON) {
                        semicolonCount++;
                    }
                }
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                ForLoopTree newForLoop;
                switch (semicolonCount) {
                    case 0: {
                        newForLoop =
                                make.addForLoopInitializer(currentForLoop, make.ExpressionStatement(methodInvocation));
                        break;
                    }
                    case 1: {
                        newForLoop = make.ForLoop(
                                currentForLoop.getInitializer(),
                                methodInvocation,
                                currentForLoop.getUpdate(),
                                currentForLoop.getStatement());
                        break;
                    }
                    default: {
                        newForLoop = make.addForLoopUpdate(currentForLoop, make.ExpressionStatement(methodInvocation));
                    }
                }
                copy.rewrite(currentForLoop, newForLoop);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void insertMethodInvocation(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.METHOD_INVOCATION) {
                    return;
                }
                MethodInvocationTree currentMethodInvocation = (MethodInvocationTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                int insertIndex = findInsertIndexForInvocationArgument(currentMethodInvocation);
                MethodInvocationTree newMethodInvocation =
                        make.insertMethodInvocationArgument(currentMethodInvocation, insertIndex, methodInvocation);
                copy.rewrite(currentMethodInvocation, newMethodInvocation);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private int findInsertIndexForInvocationArgument(MethodInvocationTree methodInvocationTree) {
        return findInsertIndexForArgument(methodInvocationTree.getArguments());
    }

    private int findInsertIndexForArgument(List<? extends ExpressionTree> arguments) {
        if (arguments.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i).toString();
            if (argument.contains(ConstantDataManager.PARENTHESIZED_ERROR)
                    || argument.contains(ConstantDataManager.ANGLED_ERROR)) {
                return i;
            }
        }
        return -1;
    }

    public void insertNewClass(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.NEW_CLASS) {
                    return;
                }
                NewClassTree currentNewClass = (NewClassTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                int insertIndex = findInsertIndexForInvocationArgument(currentNewClass);
                NewClassTree newNewClass =
                        make.insertNewClassArgument(currentNewClass, insertIndex, methodInvocation);
                copy.rewrite(currentNewClass, newNewClass);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private int findInsertIndexForInvocationArgument(NewClassTree newClassTree) {
        return findInsertIndexForArgument(newClassTree.getArguments());
    }

    public void insertConditionalStatement(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.CONDITIONAL_EXPRESSION) {
                    return;
                }
                ConditionalExpressionTree currentConditionalOperator = (ConditionalExpressionTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                ExpressionTree methodInvocation = createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
                TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
                sequence.move(abbreviation.getStartOffset());
                boolean questionFound = false;
                boolean colonFound = false;
                while (sequence.moveNext() && sequence.token().id() != JavaTokenId.SEMICOLON) {
                    TokenId tokenId = sequence.token().id();
                    if (tokenId == JavaTokenId.QUESTION) {
                        questionFound = true;
                    } else if (tokenId == JavaTokenId.COLON) {
                        colonFound = true;
                    }
                }
                ConditionalExpressionTree newConditionalOperator;
                if (colonFound) {
                    if (questionFound) {
                        newConditionalOperator =
                                make.ConditionalExpression(
                                        methodInvocation,
                                        currentConditionalOperator.getTrueExpression(),
                                        currentConditionalOperator.getFalseExpression());
                    } else {
                        newConditionalOperator =
                                make.ConditionalExpression(
                                        currentConditionalOperator.getCondition(),
                                        methodInvocation,
                                        currentConditionalOperator.getFalseExpression());
                    }
                } else {
                    newConditionalOperator =
                            make.ConditionalExpression(
                                    currentConditionalOperator.getCondition(),
                                    currentConditionalOperator.getTrueExpression(),
                                    methodInvocation);
                }
                copy.rewrite(currentConditionalOperator, newConditionalOperator);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
