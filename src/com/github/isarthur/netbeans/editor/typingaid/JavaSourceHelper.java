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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Statement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.spi.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
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
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import org.netbeans.api.java.source.WorkingCopy;
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
        this.abbreviation = JavaAbbreviation.getInstance();
        this.abbreviation.setContent(abbreviation.getContent());
        this.abbreviation.setStartOffset(abbreviation.getStartOffset());
    }

    List<Element> getElementsByAbbreviation(CompilationController controller) {
        List<Element> localElements = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        Iterable<? extends Element> localMembersAndVars =
                elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                    return (!elements.isDeprecated(e))
                            && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                            && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                            && getRequiredLocalElementKinds().contains(e.getKind());
                });
        localMembersAndVars.forEach(localElements::add);
        localElements.removeIf(element -> {
            String elementName = element.getSimpleName().toString();
            String elementAbbreviation = StringUtilities.getElementAbbreviation(elementName);
            return !elementAbbreviation.equals(abbreviation.getScope());
        });
        return Collections.unmodifiableList(localElements);
    }

    JavaSource getJavaSourceForDocument(Document document) {
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

    void moveStateToParsedPhase(CompilationController controller) throws IOException {
        Phase phase = controller.toPhase(Phase.PARSED);
        if (phase.compareTo(Phase.PARSED) < 0) {
            throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_PARSED_PHASE);
        }
    }

    private TypeMirror getTypeInContext(CompilationController controller) {
        try {
            controller.toPhase(Phase.RESOLVED);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        Trees trees = controller.getTrees();
        Types types = controller.getTypes();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return null;
        }
        Tree currentTree = currentPath.getLeaf();
        switch (currentTree.getKind()) {
            case ASSIGNMENT: {
                AssignmentTree assignmentTree = (AssignmentTree) currentTree;
                ExpressionTree variable = assignmentTree.getVariable();
                TreePath path = TreePath.getPath(currentPath, variable);
                return trees.getElement(path).asType();
            }
            case BLOCK:
            case PARENTHESIZED: {
                return null;
            }
            case CASE: {
                TreePath switchPath = treeUtilities.getPathElementOfKind(Tree.Kind.SWITCH, currentPath);
                if (switchPath == null) {
                    break;
                }
                SwitchTree switchTree = (SwitchTree) switchPath.getLeaf();
                ExpressionTree expression = switchTree.getExpression();
                TreePath expressionPath = TreePath.getPath(switchPath, expression);
                return trees.getTypeMirror(expressionPath);
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
                return types.getPrimitiveType(TypeKind.DOUBLE);
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
                return types.getPrimitiveType(TypeKind.LONG);
            }
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
            case LOGICAL_COMPLEMENT: {
                return types.getPrimitiveType(TypeKind.BOOLEAN);
            }
            case MEMBER_SELECT: {
                ExpressionTree expression = ((MemberSelectTree) currentTree).getExpression();
                TreePath path = TreePath.getPath(currentPath, expression);
                return trees.getTypeMirror(path);
            }
            case METHOD_INVOCATION: {
                int insertIndex = findIndexOfCurrentArgumentInMethod((MethodInvocationTree) currentTree);
                Element element = trees.getElement(currentPath);
                if (element.getKind() == ElementKind.METHOD) {
                    List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                    if (insertIndex != -1) {
                        VariableElement parameter = parameters.get(insertIndex);
                        return parameter.asType();
                    }
                }
                return null;
            }
            case VARIABLE: {
                VariableTree variableTree = (VariableTree) currentTree;
                Tree type = variableTree.getType();
                TreePath path = TreePath.getPath(currentPath, type);
                return trees.getElement(path).asType();
            }
        }
        return null;
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

    private Set<String> getVariableNames(TypeMirror typeMirror, CompilationController controller) {
        Set<String> names = new HashSet<>();
        List<Element> localElements = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
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
                Collections.emptySet(), null, null, controller.getTypes(), controller.getElements(), localElements,
                CodeStyle.getDefault(document)).iterator();
        while (nameSuggestions.hasNext()) {
            names.add(nameSuggestions.next());
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
        return Collections.unmodifiableSet(elementKinds);
    }

    List<TypeElement> collectTypesByAbbreviation(CompilationController controller) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        ClasspathInfo classpathInfo = javaSource.getClasspathInfo();
        ClassIndex classIndex = classpathInfo.getClassIndex();
        Set<ElementHandle<TypeElement>> declaredTypes = classIndex.getDeclaredTypes(
                abbreviation.getScope().toUpperCase(),
                ClassIndex.NameKind.CAMEL_CASE,
                EnumSet.of(ClassIndex.SearchScope.SOURCE, ClassIndex.SearchScope.DEPENDENCIES));
        List<TypeElement> typeElements = new ArrayList<>();
        Elements elements = controller.getElements();
        declaredTypes.forEach(type -> {
            TypeElement typeElement = type.resolve(controller);
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
        return Collections.unmodifiableList(typeElements);
    }

    List<MethodInvocation> collectMethodInvocations(List<Element> elements, CompilationController controller) {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getAllNonStaticMethodsInClassAndSuperclasses(element, controller);
            methods = getMethodsByAbbreviation(methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodInvocations.add(new MethodInvocation(element, method, arguments, this));
            });
        });
        Collections.sort(methodInvocations);
        return Collections.unmodifiableList(methodInvocations);
    }

    private List<ExecutableElement> getAllNonStaticMethodsInClassAndSuperclasses(
            Element element, CompilationController controller) {
        List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(element, controller);
        methods = filterNonStaticMethods(methods);
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> getAllMethodsInClassAndSuperclasses(
            Element element, CompilationController controller) {
        List<ExecutableElement> methods = new ArrayList<>();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        TypeMirror typeMirror = element.asType();
        Iterable<? extends Element> members;
        try {
            members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
            });
        } catch (AssertionError error) {
            return Collections.emptyList();
        }
        members.forEach(member -> methods.add((ExecutableElement) member));
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> filterNonStaticMethods(List<ExecutableElement> methods) {
        return methods.stream()
                .filter(method -> (!method.getModifiers().contains(Modifier.STATIC)))
                .collect(Collectors.toList());
    }

    public List<CodeFragment> insertCodeFragment(CodeFragment fragment) {
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreeMaker make = copy.getTreeMaker();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                Tree.Kind kind = currentTree.getKind();
                switch (kind) {
                    case AND:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.AND);
                        break;
                    case AND_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.AND_ASSIGNMENT);
                        break;
                    case ASSIGNMENT:
                        insertAssignmentTree(fragment, copy, make);
                        break;
                    case BLOCK:
                        insertBlockTree(fragment, copy, make);
                        break;
                    case CASE:
                        insertCaseTree(fragment, copy, make);
                        break;
                    case CLASS:
                        insertClassEnumOrInterfaceTree(fragment, copy, make);
                        break;
                    case COMPILATION_UNIT:
                        insertCompilationUnitTree(fragment, copy, make);
                        break;
                    case CONDITIONAL_AND:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.CONDITIONAL_AND);
                        break;
                    case CONDITIONAL_EXPRESSION:
                        insertConditionalExpressionTree(fragment, copy, make);
                        break;
                    case CONDITIONAL_OR:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.CONDITIONAL_OR);
                        break;
                    case DIVIDE:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.DIVIDE);
                        break;
                    case DIVIDE_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.DIVIDE_ASSIGNMENT);
                        break;
                    case ENUM:
                        insertClassEnumOrInterfaceTree(fragment, copy, make);
                        break;
                    case EQUAL_TO:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.EQUAL_TO);
                        break;
                    case FOR_LOOP:
                        insertForLoopTree(fragment, copy, make);
                        break;
                    case GREATER_THAN:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.GREATER_THAN);
                        break;
                    case GREATER_THAN_EQUAL:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.GREATER_THAN_EQUAL);
                        break;
                    case INTERFACE:
                        insertClassEnumOrInterfaceTree(fragment, copy, make);
                        break;
                    case LEFT_SHIFT:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.LEFT_SHIFT);
                        break;
                    case LEFT_SHIFT_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.LEFT_SHIFT_ASSIGNMENT);
                        break;
                    case LESS_THAN:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.LESS_THAN);
                        break;
                    case LESS_THAN_EQUAL:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.LESS_THAN_EQUAL);
                        break;
                    case LOGICAL_COMPLEMENT:
                        insertUnaryTree(fragment, copy, make, Tree.Kind.LOGICAL_COMPLEMENT);
                        break;
                    case MEMBER_SELECT:
                        insertMemberSelectTree(fragment, copy, make);
                        break;
                    case METHOD:
                        insertMethodTree(fragment, copy, make);
                        break;
                    case METHOD_INVOCATION:
                        insertMethodInvocationTree(fragment, copy, make);
                        break;
                    case MINUS:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.MINUS);
                        break;
                    case MINUS_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.MINUS_ASSIGNMENT);
                        break;
                    case MODIFIERS:
                        insertModifiersTree(fragment, copy, make);
                        break;
                    case MULTIPLY:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.MULTIPLY);
                        break;
                    case MULTIPLY_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.MULTIPLY_ASSIGNMENT);
                        break;
                    case NEW_CLASS:
                        insertNewClassTree(fragment, copy, make);
                        break;
                    case NOT_EQUAL_TO:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.NOT_EQUAL_TO);
                        break;
                    case OR:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.OR);
                        break;
                    case OR_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.OR_ASSIGNMENT);
                        break;
                    case PARENTHESIZED:
                        insertParenthesizedTree(fragment, copy, make);
                        break;
                    case PLUS:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.PLUS);
                        break;
                    case PLUS_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.PLUS_ASSIGNMENT);
                        break;
                    case REMAINDER:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.REMAINDER);
                        break;
                    case REMAINDER_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.REMAINDER_ASSIGNMENT);
                        break;
                    case RETURN:
                        insertReturnTree(fragment, copy, make);
                        break;
                    case RIGHT_SHIFT:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.RIGHT_SHIFT);
                        break;
                    case RIGHT_SHIFT_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.RIGHT_SHIFT_ASSIGNMENT);
                        break;
                    case UNARY_MINUS:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.UNARY_MINUS);
                        break;
                    case UNARY_PLUS:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.UNARY_PLUS);
                        break;
                    case UNSIGNED_RIGHT_SHIFT:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.UNSIGNED_RIGHT_SHIFT);
                        break;
                    case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT);
                        break;
                    case VARIABLE:
                        insertVariableTree(fragment, copy, make);
                        break;
                    case XOR:
                        insertBinaryTree(fragment, copy, make, Tree.Kind.XOR);
                        break;
                    case XOR_ASSIGNMENT:
                        insertCompoundAssignmentTree(fragment, copy, make, Tree.Kind.XOR_ASSIGNMENT);
                        break;
                }
            });
            modificationResult.commit();
            return Collections.singletonList(fragment);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return Collections.emptyList();
        }
    }

    public List<CodeFragment> insertKeyword(Keyword keyword) {
        switch (keyword.getName()) {
            case "assert": //NOI18N
                return insertAssertStatement();
            case "break": //NOI18N
                return insertBreakStatement();
            case "case": //NOI18N
                return insertCaseStatement();
            case "catch": //NOI18N
                return insertCatchTree();
            case "class": //NOI18N
                return insertClassDeclaration();
            case "continue": //NOI18N
                return insertContinueStatement();
            case "do": //NOI18N
                return insertDoWhileStatement();
            case "else": //NOI18N
                return insertElseTree();
            case "enum": //NOI18N
                return insertEnumDeclaration();
            case "extends": //NOI18N
                return insertExtendsTree();
            case "finally": //NOI18N
                return insertFinallyTree();
            case "for": //NOI18N
                return insertForStatement();
            case "if": //NOI18N
                return insertIfStatement();
            case "implements": //NOI18N
                return insertImplementsTree();
            case "import": //NOI18N
                return insertImportStatement();
            case "interface": //NOI18N
                return insertInterfaceDeclaration();
            case "return": //NOI18N
                return insertReturnStatement();
            case "switch": //NOI18N
                return insertSwitchStatement();
            case "throw": //NOI18N
                return insertThrowStatement();
            case "try": //NOI18N
                return insertTryStatement();
            case "while": //NOI18N
                return insertWhileStatement();
            default:
                return insertCodeFragment(keyword);
        }
    }

    List<MethodInvocation> collectLocalMethodInvocations(CompilationController controller) {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses(controller);
        methods = getMethodsByAbbreviation(methods);
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            methodInvocations.add(new MethodInvocation(null, method, arguments, this));
        });
        Collections.sort(methodInvocations);
        return Collections.unmodifiableList(methodInvocations);
    }

    private List<ExecutableElement> getMethodsInCurrentAndSuperclasses(CompilationController controller) {
        List<ExecutableElement> methods = new ArrayList<>();
        Elements elements = controller.getElements();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        TypeMirror typeMirror = getTypeMirrorOfCurrentClass(controller);
        if (typeMirror == null) {
            return Collections.emptyList();
        }
        Iterable<? extends Element> members = elementUtilities.getMembers(typeMirror, (e, t) -> {
            return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
        });
        methods.addAll(ElementFilter.methodsIn(members));
        return Collections.unmodifiableList(methods);
    }

    private TypeMirror getTypeMirrorOfCurrentClass(CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        Tree tree = compilationUnit.getTypeDecls().get(0);
        if (tree.getKind() == Tree.Kind.CLASS) {
            return trees.getTypeMirror(TreePath.getPath(compilationUnit, tree));
        }
        return null;
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
                moveStateToParsedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                List<? extends VariableElement> parameters = method.getParameters();
                parameters.stream()
                        .map(parameter -> parameter.asType())
                        .forEachOrdered(elementType -> {
                            AtomicReference<IdentifierTree> identifierTree = new AtomicReference<>();
                            VariableElement variableElement = instanceOf(elementType.toString(), "", copy); //NOI18N
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

    private VariableElement instanceOf(String typeName, String name, CompilationController controller) {
        VariableElement closest = null;
        List<Element> localElements = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        Types types = controller.getTypes();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        Iterable<? extends Element> localMembersAndVars =
                elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                    return (!elements.isDeprecated(e))
                            && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                            && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                            && getRequiredLocalElementKinds().contains(e.getKind());
                });
        localMembersAndVars.forEach(localElements::add);
        TypeMirror type = type(typeName, controller);
        if (type == null) {
            return null;
        }
        int distance = Integer.MAX_VALUE;
        for (Element element : localElements) {
            if (VariableElement.class
                    .isInstance(element)
                    && !ConstantDataManager.ANGLED_ERROR.contentEquals(element.getSimpleName())
                    && element.asType().getKind() != TypeKind.ERROR
                    && types.isAssignable(element.asType(), type)) {
                if (name.isEmpty()) {
                    return (VariableElement) element;
                }
                int d = ElementHeaders.getDistance(element.getSimpleName().toString()
                        .toLowerCase(), name.toLowerCase());
                if (isSameType(element.asType(), type, types)) {
                    d -= 1000;
                }
                if (d < distance) {
                    distance = d;
                    closest = (VariableElement) element;
                }
            }
        }
        return closest;
    }

    private TypeMirror type(String typeName, CompilationController controller) {
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        Trees trees = controller.getTrees();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        String type = typeName.trim();
        if (type.isEmpty()) {
            return null;
        }
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return null;
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
                    return trees.getTypeMirror(new TreePath(currentPath, ((VariableTree) variable).getType()));
                }
            }
        }
        return treeUtilities.parseType(type, enclosingClass);
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

    List<MethodInvocation> collectStaticMethodInvocations(CompilationController controller) {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        List<TypeElement> typeElements = collectTypesByAbbreviation(controller);
        typeElements.forEach(element -> {
            List<ExecutableElement> methods = getStaticMethodsInClass(element);
            methods = getMethodsByAbbreviation(methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodInvocations.add(new MethodInvocation(element, method, arguments, this));
            });
        });
        Collections.sort(methodInvocations);
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

    List<LocalElement> collectLocalElements(CompilationController controller) {
        List<Element> localElements = new ArrayList<>();
        List<LocalElement> result = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath.getLeaf().getKind() == Tree.Kind.BLOCK
                || TreeUtilities.CLASS_TREE_KINDS.contains(currentPath.getLeaf().getKind())) {
            return Collections.emptyList();
        }
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
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
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    List<Type> collectLocalTypes(CompilationController controller) {
        List<Element> localElements = new ArrayList<>();
        List<Type> result = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        Scope scope = treeUtilities.scopeFor(abbreviation.getStartOffset());
        if (Settings.getSettingForInternalType()) {
            CompilationUnitTree compilationUnit = controller.getCompilationUnit();
            List<? extends Tree> typeDecls = compilationUnit.getTypeDecls();
            Tree topLevelClassInterfaceOrEnumTree = typeDecls.get(0);
            Element topLevelElement = controller.getTrees().getElement(
                    TreePath.getPath(compilationUnit, topLevelClassInterfaceOrEnumTree));
            localElements.add(topLevelElement);
            Iterable<? extends Element> localMembersAndVars =
                    elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                        return (!elements.isDeprecated(e)
                                && (e.getKind() == ElementKind.CLASS
                                || e.getKind() == ElementKind.ENUM
                                || e.getKind() == ElementKind.INTERFACE));
                    });
            localMembersAndVars.forEach(localElements::add);
            localElements
                    .stream()
                    .filter(element -> StringUtilities.getElementAbbreviation(
                            element.getSimpleName().toString()).equals(abbreviation.getName()))
                    .filter(distinctByKey(Element::getSimpleName))
                    .forEach(element -> result.add(new Type(elements.getTypeElement(element.toString()))));
            Collections.sort(result);
        }
        return Collections.unmodifiableList(result);
    }

    List<Keyword> collectKeywords(CompilationController controller) {
        List<Keyword> keywords = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        TreePath parentPath;
        for (Keyword keyword : ConstantDataManager.KEYWORDS) {
            if (keyword.isAbbreviationEqualTo(abbreviation.getName())) {
                switch (keyword.getName()) {
                    case "assert": //NOI18N
                    case "do": //NOI18N
                    case "for": //NOI18N
                    case "if": //NOI18N
                    case "switch": //NOI18N
                    case "throw": //NOI18N
                    case "try": //NOI18N
                    case "while": //NOI18N
                        if (currentPath.getLeaf().getKind() == Tree.Kind.BLOCK
                                || currentPath.getLeaf().getKind() == Tree.Kind.CASE) {
                            keywords.add(keyword);
                        }
                        break;
                    case "break": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(
                                EnumSet.of(Tree.Kind.CASE, Tree.Kind.DO_WHILE_LOOP, Tree.Kind.ENHANCED_FOR_LOOP,
                                        Tree.Kind.FOR_LOOP, Tree.Kind.SWITCH, Tree.Kind.WHILE_LOOP),
                                currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "case": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(Tree.Kind.SWITCH, currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "catch": //NOI18N
                    case "finally": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(Tree.Kind.TRY, currentPath);
                        if (parentPath != null) {
                            TokenSequence<?> sequence = controller.getTokenHierarchy().tokenSequence();
                            sequence.move(abbreviation.getStartOffset());
                            moveToNextNonWhitespaceToken(sequence);
                            Token<?> token = sequence.token();
                            if (token != null) {
                                if (token.id() == JavaTokenId.CATCH) {
                                    keywords.add(keyword);
                                } else if (token.id() == JavaTokenId.LBRACE) {
                                    sequence.move(abbreviation.getStartOffset());
                                    moveToPreviousNonWhitespaceToken(sequence);
                                    token = sequence.token();
                                    if (token != null && token.id() == JavaTokenId.TRY) {
                                        keywords.add(keyword);
                                    }
                                }
                            }
                        }
                        break;
                    case "class": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case ENUM:
                            case INTERFACE:
                                TokenSequence<JavaTokenId> sequence =
                                        treeUtilities.tokensFor(currentPath.getLeaf());
                                sequence.moveStart();
                                int leftBraceOffset = Integer.MAX_VALUE;
                                while (sequence.moveNext()) {
                                    if (sequence.token().id() == JavaTokenId.LBRACE) {
                                        leftBraceOffset = sequence.offset();
                                        break;
                                    }
                                }
                                if (leftBraceOffset < abbreviation.getStartOffset()) {
                                    keywords.add(keyword);
                                }
                                break;
                            case BLOCK:
                            case COMPILATION_UNIT:
                                keywords.add(keyword);
                        }
                        break;
                    case "continue": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(
                                EnumSet.of(Tree.Kind.DO_WHILE_LOOP, Tree.Kind.ENHANCED_FOR_LOOP,
                                        Tree.Kind.FOR_LOOP, Tree.Kind.WHILE_LOOP),
                                currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "default": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(Tree.Kind.SWITCH, currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "else": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(Tree.Kind.IF, currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "enum": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case ENUM:
                            case INTERFACE:
                                TokenSequence<JavaTokenId> sequence =
                                        treeUtilities.tokensFor(currentPath.getLeaf());
                                sequence.moveStart();
                                int leftBraceOffset = Integer.MAX_VALUE;
                                while (sequence.moveNext()) {
                                    if (sequence.token().id() == JavaTokenId.LBRACE) {
                                        leftBraceOffset = sequence.offset();
                                        break;
                                    }
                                }
                                if (leftBraceOffset < abbreviation.getStartOffset()) {
                                    keywords.add(keyword);
                                }
                                break;
                            case COMPILATION_UNIT:
                                keywords.add(keyword);
                        }
                        break;
                    case "false": //NOI18N
                    case "null": //NOI18N
                    case "true": //NOI18N
                    case "this": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case ASSIGNMENT:
                            case EQUAL_TO:
                            case METHOD_INVOCATION:
                            case NEW_CLASS:
                            case NOT_EQUAL_TO:
                            case PARENTHESIZED:
                            case RETURN:
                            case VARIABLE:
                                keywords.add(keyword);
                                break;
                        }
                        break;
                    case "interface": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case ENUM:
                            case INTERFACE:
                                TokenSequence<JavaTokenId> sequence =
                                        treeUtilities.tokensFor(currentPath.getLeaf());
                                sequence.moveStart();
                                int leftBraceOffset = Integer.MAX_VALUE;
                                while (sequence.moveNext()) {
                                    if (sequence.token().id() == JavaTokenId.LBRACE) {
                                        leftBraceOffset = sequence.offset();
                                        break;
                                    }
                                }
                                if (leftBraceOffset < abbreviation.getStartOffset()) {
                                    keywords.add(keyword);
                                }
                                break;
                            case COMPILATION_UNIT:
                                keywords.add(keyword);
                        }
                        break;
                    case "extends": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case INTERFACE:
                                TokenSequence<JavaTokenId> sequence =
                                        treeUtilities.tokensFor(currentPath.getLeaf());
                                sequence.moveStart();
                                int leftBraceOffset = -1;
                                while (sequence.moveNext()) {
                                    if (sequence.token().id() == JavaTokenId.LBRACE) {
                                        leftBraceOffset = sequence.offset();
                                        break;
                                    }
                                }
                                if (leftBraceOffset >= abbreviation.getStartOffset()) {
                                    keywords.add(keyword);
                                }
                                break;
                            case TYPE_PARAMETER:
                                keywords.add(keyword);
                                break;
                        }
                        break;
                    case "implements": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case ENUM:
                                TokenSequence<JavaTokenId> sequence =
                                        treeUtilities.tokensFor(currentPath.getLeaf());
                                sequence.moveStart();
                                int leftBraceOffset = Integer.MAX_VALUE;
                                while (sequence.moveNext()) {
                                    if (sequence.token().id() == JavaTokenId.LBRACE) {
                                        leftBraceOffset = sequence.offset();
                                        break;
                                    }
                                }
                                if (abbreviation.getStartOffset() <= leftBraceOffset) {
                                    keywords.add(keyword);
                                }
                                break;
                        }
                        break;
                    case "import": //NOI18N
                        if (currentPath.getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
                            keywords.add(keyword);
                        }
                        break;
                    case "instanceof": //NOI18N
                        break;
                    case "return": //NOI18N
                        parentPath = treeUtilities.getPathElementOfKind(Tree.Kind.METHOD, currentPath);
                        if (parentPath != null) {
                            keywords.add(keyword);
                        }
                        break;
                    case "void": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case INTERFACE:
                            case ENUM:
                                keywords.add(keyword);
                                break;
                        }
                        break;
                    case "throws": //NOI18N
                        if (currentPath.getLeaf().getKind() == Tree.Kind.METHOD) {
                            keywords.add(keyword);
                        }
                        break;
                    case "String": //NOI18N
                        switch (currentPath.getLeaf().getKind()) {
                            case CLASS:
                            case BLOCK:
                            case ENUM:
                            case METHOD:
                            case VARIABLE:
                                keywords.add(keyword);
                                break;
                        }
                        break;
                }
            }
        }
        Collections.sort(keywords);
        return Collections.unmodifiableList(keywords);
    }

    List<com.github.isarthur.netbeans.editor.typingaid.codefragment.PrimitiveType> collectPrimitiveTypes(
            CompilationController controller) {
        List<com.github.isarthur.netbeans.editor.typingaid.codefragment.PrimitiveType> primitiveTypes =
                new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        switch (currentPath.getLeaf().getKind()) {
            case CLASS:
            case BLOCK:
            case ENUM:
            case METHOD:
            case VARIABLE:
                ConstantDataManager.PRIMITIVE_TYPES.forEach(primitiveType -> {
                    if (primitiveType.isAbbreviationEqualTo(abbreviation.getName())) {
                        primitiveTypes.add(primitiveType);
                    }
                });
                break;
        }
        Collections.sort(primitiveTypes);
        return Collections.unmodifiableList(primitiveTypes);
    }

    List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> collectModifiers(
            CompilationController controller) {
        List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        Tree.Kind currentContext = currentTree.getKind();
        TokenSequence<?> sequence = controller.getTokenHierarchy().tokenSequence();
        sequence.move(abbreviation.getStartOffset());
        TokenId tokenId;
        TreePath path;
        ModifiersTree modifiersTree;
        switch (currentContext) {
            case BLOCK:
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.VARIABLE, Tree.Kind.CLASS),
                        treeUtilities.pathFor(sequence.offset()));
                if (path != null) {
                    switch (path.getLeaf().getKind()) {
                        case CLASS:
                            ClassTree clazz = (ClassTree) path.getLeaf();
                            sequence = treeUtilities.tokensFor(clazz);
                            sequence.moveStart();
                            while (sequence.moveNext() && sequence.token().id() != JavaTokenId.LBRACE) {
                            }
                            int[] classSpan = treeUtilities.findBodySpan(clazz);
                            if (abbreviation.getStartOffset() < classSpan[0]
                                    || (abbreviation.getStartOffset() >= classSpan[0]
                                    && abbreviation.getStartOffset() < sequence.offset())) {
                                modifiersTree = clazz.getModifiers();
                                filterMethodLocalInnerClassModifiers(modifiersTree, modifiers);
                            }
                            break;
                        case VARIABLE:
                            VariableTree variable = (VariableTree) path.getLeaf();
                            modifiersTree = variable.getModifiers();
                            if (!modifiersTree.getFlags().contains(Modifier.FINAL)
                                    && StringUtilities.getElementAbbreviation("final").equals(abbreviation.getName())) {
                                modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier("final")); //NOI18N
                            }
                            break;
                    }
                }
                break;
            case CLASS:
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.VARIABLE, Tree.Kind.METHOD, Tree.Kind.CLASS, Tree.Kind.INTERFACE,
                                Tree.Kind.ENUM),
                        treeUtilities.pathFor(sequence.offset()));
                TreePath parentPath;
                if (path != null) {
                    switch (path.getLeaf().getKind()) {
                        case CLASS:
                            ClassTree clazz = (ClassTree) path.getLeaf();
                            sequence = treeUtilities.tokensFor(clazz);
                            sequence.moveStart();
                            while (sequence.moveNext() && sequence.token().id() != JavaTokenId.LBRACE) {
                            }
                            int[] classSpan = treeUtilities.findBodySpan(clazz);
                            if (abbreviation.getStartOffset() < classSpan[0]
                                    || (abbreviation.getStartOffset() >= classSpan[0]
                                    && abbreviation.getStartOffset() < sequence.offset())) {
                                modifiersTree = clazz.getModifiers();
                                parentPath = path.getParentPath();
                                if (parentPath != null) {
                                    Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                    switch (parentKind) {
                                        case BLOCK:
                                            filterMethodLocalInnerClassModifiers(modifiersTree, modifiers);
                                            break;
                                        case COMPILATION_UNIT:
                                            filterTopLevelClassModifiers(modifiersTree, modifiers);
                                            break;
                                        default:
                                            filterInnerClassModifiers(modifiersTree, modifiers);
                                    }
                                }
                            }
                            break;
                        case ENUM:
                            ClassTree enumeration = (ClassTree) path.getLeaf();
                            modifiersTree = enumeration.getModifiers();
                            parentPath = path.getParentPath();
                            if (parentPath != null) {
                                Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                switch (parentKind) {
                                    case COMPILATION_UNIT:
                                        filterTopLevelEnumModifiers(modifiersTree, modifiers);
                                        break;
                                    default:
                                        filterInnerEnumModifiers(modifiersTree, modifiers);
                                }
                            }
                            break;
                        case INTERFACE:
                            ClassTree interfaze = (ClassTree) path.getLeaf();
                            modifiersTree = interfaze.getModifiers();
                            parentPath = path.getParentPath();
                            if (parentPath != null) {
                                Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                switch (parentKind) {
                                    case COMPILATION_UNIT:
                                        filterTopLevelInterfaceModifiers(modifiersTree, modifiers);
                                        break;
                                    default:
                                        filterInnerInterfaceModifiers(modifiersTree, modifiers);
                                }
                            }
                            break;
                        case METHOD:
                            MethodTree method = (MethodTree) path.getLeaf();
                            modifiersTree = method.getModifiers();
                            filterMethodModifiers(modifiersTree, modifiers);
                            break;
                        case VARIABLE:
                            VariableTree variable = (VariableTree) path.getLeaf();
                            modifiersTree = variable.getModifiers();
                            filterFieldModifiers(modifiersTree, modifiers);
                            break;
                    }
                }
                break;
            case COMPILATION_UNIT:
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM),
                        treeUtilities.pathFor(sequence.offset()));
                if (path != null) {
                    switch (path.getLeaf().getKind()) {
                        case CLASS:
                            ClassTree clazz = (ClassTree) path.getLeaf();
                            modifiersTree = clazz.getModifiers();
                            filterTopLevelClassModifiers(modifiersTree, modifiers);
                            break;
                        case ENUM:
                            ClassTree enumeration = (ClassTree) path.getLeaf();
                            modifiersTree = enumeration.getModifiers();
                            filterTopLevelEnumModifiers(modifiersTree, modifiers);
                            break;
                        case INTERFACE:
                            ClassTree interfaze = (ClassTree) path.getLeaf();
                            modifiersTree = interfaze.getModifiers();
                            filterTopLevelInterfaceModifiers(modifiersTree, modifiers);
                            break;
                    }
                }
                break;
            case ENUM:
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.ENUM),
                        treeUtilities.pathFor(sequence.offset()));
                if (path != null) {
                    ClassTree enumeration = (ClassTree) path.getLeaf();
                    modifiersTree = enumeration.getModifiers();
                    parentPath = path.getParentPath();
                    if (parentPath != null) {
                        Tree.Kind parentKind = parentPath.getLeaf().getKind();
                        switch (parentKind) {
                            case CLASS:
                            case INTERFACE:
                                filterInnerEnumModifiers(modifiersTree, modifiers);
                                break;
                            case COMPILATION_UNIT:
                                filterTopLevelEnumModifiers(modifiersTree, modifiers);
                                break;
                        }
                    }
                }
                break;
            case INTERFACE:
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.INTERFACE),
                        treeUtilities.pathFor(sequence.offset()));
                if (path != null) {
                    ClassTree interfaze = (ClassTree) path.getLeaf();
                    modifiersTree = interfaze.getModifiers();
                    parentPath = path.getParentPath();
                    if (parentPath != null) {
                        Tree.Kind parentKind = parentPath.getLeaf().getKind();
                        switch (parentKind) {
                            case CLASS:
                            case INTERFACE:
                                filterInnerInterfaceModifiers(modifiersTree, modifiers);
                                break;
                            case COMPILATION_UNIT:
                                filterTopLevelInterfaceModifiers(modifiersTree, modifiers);
                                break;
                        }
                    }
                }
                break;
            case METHOD:
                tokenId = moveToPreviousNonWhitespaceToken(sequence);
                if (isModifier(tokenId)) {
                    MethodTree method = (MethodTree) currentTree;
                    modifiersTree = method.getModifiers();
                    filterMethodModifiers(modifiersTree, modifiers);
                } else if (tokenId == JavaTokenId.COMMA || tokenId == JavaTokenId.LPAREN) {
                    moveToNextNonWhitespaceToken(sequence);
                    TreePath parameterPath = treeUtilities.pathFor(sequence.offset());
                    if (parameterPath != null) {
                        Tree.Kind kind = parameterPath.getLeaf().getKind();
                        if (kind == Tree.Kind.VARIABLE) {
                            VariableTree variable = (VariableTree) parameterPath.getLeaf();
                            modifiersTree = variable.getModifiers();
                            if (StringUtilities.getElementAbbreviation("final").equals(abbreviation.getName()) //NOI18N
                                    && !modifiersTree.getFlags().contains(Modifier.FINAL)) {
                                modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier("final")); //NOI18N
                            }
                        }
                    }
                }
                break;
            case MODIFIERS:
                path = treeUtilities.getPathElementOfKind(
                        EnumSet.of(Tree.Kind.VARIABLE, Tree.Kind.METHOD, Tree.Kind.CLASS, Tree.Kind.INTERFACE,
                                Tree.Kind.ENUM),
                        currentPath);
                if (path != null) {
                    switch (path.getLeaf().getKind()) {
                        case CLASS:
                            ClassTree clazz = (ClassTree) path.getLeaf();
                            modifiersTree = clazz.getModifiers();
                            parentPath = path.getParentPath();
                            if (parentPath != null) {
                                Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                switch (parentKind) {
                                    case BLOCK:
                                        filterMethodLocalInnerClassModifiers(modifiersTree, modifiers);
                                        break;
                                    case COMPILATION_UNIT:
                                        filterTopLevelClassModifiers(modifiersTree, modifiers);
                                        break;
                                    default:
                                        filterInnerClassModifiers(modifiersTree, modifiers);
                                }
                            }
                            break;
                        case ENUM:
                            ClassTree enumeration = (ClassTree) path.getLeaf();
                            modifiersTree = enumeration.getModifiers();
                            parentPath = path.getParentPath();
                            if (parentPath != null) {
                                Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                switch (parentKind) {
                                    case COMPILATION_UNIT:
                                        filterTopLevelEnumModifiers(modifiersTree, modifiers);
                                        break;
                                    default:
                                        filterInnerEnumModifiers(modifiersTree, modifiers);
                                }
                            }
                            break;
                        case INTERFACE:
                            ClassTree interfaze = (ClassTree) path.getLeaf();
                            modifiersTree = interfaze.getModifiers();
                            parentPath = path.getParentPath();
                            if (parentPath != null) {
                                Tree.Kind parentKind = parentPath.getLeaf().getKind();
                                switch (parentKind) {
                                    case COMPILATION_UNIT:
                                        filterTopLevelInterfaceModifiers(modifiersTree, modifiers);
                                        break;
                                    default:
                                        filterInnerInterfaceModifiers(modifiersTree, modifiers);
                                }
                            }
                            break;
                        case METHOD:
                            MethodTree method = (MethodTree) path.getLeaf();
                            modifiersTree = method.getModifiers();
                            filterMethodModifiers(modifiersTree, modifiers);
                            break;
                        case VARIABLE:
                            VariableTree variable = (VariableTree) path.getLeaf();
                            modifiersTree = variable.getModifiers();
                            filterFieldModifiers(modifiersTree, modifiers);
                            break;
                    }
                }
                break;
            case VARIABLE:
                tokenId = moveToPreviousNonWhitespaceToken(sequence);
                if (isModifier(tokenId)) {
                    VariableTree variable = (VariableTree) currentTree;
                    modifiersTree = variable.getModifiers();
                    filterFieldModifiers(modifiersTree, modifiers);
                }
                break;
        }
        Collections.sort(modifiers);
        return Collections.unmodifiableList(modifiers);
    }

    private TokenId moveToNextNonWhitespaceToken(TokenSequence<?> sequence) {
        while (sequence.moveNext() && sequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        return sequence.token().id();
    }

    private TokenId moveToPreviousNonWhitespaceToken(TokenSequence<?> sequence) {
        while (sequence.movePrevious() && sequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        return sequence.token().id();
    }

    private boolean isModifier(TokenId tokenId) {
        return tokenId == JavaTokenId.ABSTRACT
                || tokenId == JavaTokenId.FINAL
                || tokenId == JavaTokenId.NATIVE
                || tokenId == JavaTokenId.PRIVATE
                || tokenId == JavaTokenId.PROTECTED
                || tokenId == JavaTokenId.PUBLIC
                || tokenId == JavaTokenId.STATIC
                || tokenId == JavaTokenId.STRICTFP
                || tokenId == JavaTokenId.SYNCHRONIZED
                || tokenId == JavaTokenId.TRANSIENT
                || tokenId == JavaTokenId.VOLATILE;
    }

    private void filterTopLevelClassModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)
                || modifiersTree.getFlags().contains(Modifier.FINAL)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("final") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("strictfp"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterTopLevelInterfaceModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("strictfp"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterTopLevelEnumModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("public") //NOI18N
                            || modifier.equals("strictfp"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterInnerInterfaceModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STATIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PUBLIC)
                || modifiersTree.getFlags().contains(Modifier.PROTECTED)
                || modifiersTree.getFlags().contains(Modifier.PRIVATE)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("protected") //NOI18N
                            || modifier.equals("private") //NOI18N
                            || modifier.equals("strictfp") //NOI18N
                            || modifier.equals("static"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterInnerEnumModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STATIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PUBLIC)
                || modifiersTree.getFlags().contains(Modifier.PROTECTED)
                || modifiersTree.getFlags().contains(Modifier.PRIVATE)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("public") //NOI18N
                            || modifier.equals("protected") //NOI18N
                            || modifier.equals("private") //NOI18N
                            || modifier.equals("strictfp") //NOI18N
                            || modifier.equals("static"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterMethodLocalInnerClassModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)
                || modifiersTree.getFlags().contains(Modifier.FINAL)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("final") //NOI18N
                            || modifier.equals("strictfp"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterInnerClassModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)
                || modifiersTree.getFlags().contains(Modifier.FINAL)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PRIVATE)
                || modifiersTree.getFlags().contains(Modifier.PROTECTED)
                || modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STATIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("final") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("protected") //NOI18N
                            || modifier.equals("private") //NOI18N
                            || modifier.equals("strictfp") //NOI18N
                            || modifier.equals("static"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterMethodModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.ABSTRACT)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.NATIVE)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STRICTFP)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.FINAL)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("native") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.NATIVE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PRIVATE)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("native") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.NATIVE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PROTECTED)
                || modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("native") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.NATIVE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.STATIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("abstract") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.ABSTRACT))
                            || (modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("native") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.NATIVE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("native") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.NATIVE))
                            || (modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("strictfp") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STRICTFP))
                            || (modifier.equals("synchronized") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.SYNCHRONIZED))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("abstract") //NOI18N
                            || modifier.equals("final") //NOI18N
                            || modifier.equals("native") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("protected") //NOI18N
                            || modifier.equals("private") //NOI18N
                            || modifier.equals("strictfp") //NOI18N
                            || modifier.equals("static") //NOI18N
                            || modifier.equals("synchronized"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    private void filterFieldModifiers(ModifiersTree modifiersTree,
            List<com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier> modifiers) {
        if (modifiersTree.getFlags().contains(Modifier.FINAL)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("transient") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.TRANSIENT))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.VOLATILE)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("private") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("protected") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("public") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.PUBLIC)
                            && !modifiersTree.getFlags().contains(Modifier.PROTECTED)
                            && !modifiersTree.getFlags().contains(Modifier.PRIVATE))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("transient") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.TRANSIENT))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else if (modifiersTree.getFlags().contains(Modifier.PRIVATE)
                || modifiersTree.getFlags().contains(Modifier.PROTECTED)
                || modifiersTree.getFlags().contains(Modifier.PUBLIC)) {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && ((modifier.equals("final") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.FINAL))
                            || (modifier.equals("static") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.STATIC))
                            || (modifier.equals("transient") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.TRANSIENT))
                            || (modifier.equals("volatile") //NOI18N
                            && !modifiersTree.getFlags().contains(Modifier.VOLATILE))))
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        } else {
            ConstantDataManager.MODIFIERS.stream()
                    .filter(modifier ->
                            StringUtilities.getElementAbbreviation(modifier).equals(abbreviation.getName())
                            && (modifier.equals("final") //NOI18N
                            || modifier.equals("public") //NOI18N
                            || modifier.equals("protected") //NOI18N
                            || modifier.equals("private") //NOI18N
                            || modifier.equals("static") //NOI18N
                            || modifier.equals("transient") //NOI18N
                            || modifier.equals("volatile"))) //NOI18N
                    .forEach(modifier ->
                            modifiers.add(new com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier(modifier)));
        }
    }

    boolean isMemberSelection(CompilationController controller) {
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return false;
        }
        return currentPath.getLeaf().getKind() == Tree.Kind.MEMBER_SELECT;
    }

    boolean isCaseLabel(CompilationController controller) {
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return false;
        } else {
            TokenSequence<?> sequence = controller.getTokenHierarchy().tokenSequence();
            sequence.move(abbreviation.getStartOffset());
            while (sequence.movePrevious() && sequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            Token<?> token = sequence.token();
            if (token == null) {
                return false;
            }
            return token.id() == JavaTokenId.CASE;
        }
    }

    private boolean isCaseStatement(CompilationController controller) {
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return false;
        } else {
            TokenSequence<?> sequence = controller.getTokenHierarchy().tokenSequence();
            sequence.move(abbreviation.getStartOffset());
            while (sequence.movePrevious() && sequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            return currentPath.getLeaf().getKind() == Tree.Kind.CASE
                    && sequence.token().id() != JavaTokenId.CASE;
        }
    }

    List<LocalElement> collectEnumConstantsOfSwitchExpressionType(CompilationController controller) {
        List<LocalElement> result = new ArrayList<>();
        Types types = controller.getTypes();
        TypeMirror type = getTypeInContext(controller);
        if (type == null) {
            return Collections.emptyList();
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return Collections.emptyList();
        }
        List<VariableElement> enumConstants = getEnumConstants(typeElement, controller);
        enumConstants = getFieldsOrEnumConstantsByAbbreviation(enumConstants);
        enumConstants.forEach(enumConstant -> result.add(new LocalElement(enumConstant)));
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    List<MethodInvocation> collectChainedMethodInvocations(CompilationController controller) {
        List<MethodInvocation> methodInvocations = new ArrayList<>();
        Types types = controller.getTypes();
        TypeMirror type = getTypeInContext(controller);
        if (type == null) {
            return Collections.emptyList();
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return Collections.emptyList();
        }
        List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(typeElement, controller);
        methods = getMethodsByAbbreviation(methods);
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            methodInvocations.add(new MethodInvocation(null, method, arguments, this));
        });
        Collections.sort(methodInvocations);
        return Collections.unmodifiableList(methodInvocations);
    }

    List<FieldAccess> collectFieldAccesses(CompilationController controller) {
        List<FieldAccess> fieldAccesses = new ArrayList<>();
        List<TypeElement> typeElements = collectTypesByAbbreviation(controller);
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
                        fieldAccesses.add(new FieldAccess(typeElement, element));
                    }
                });
            } catch (AssertionError ex) {
            }
        });
        Collections.sort(fieldAccesses);
        return Collections.unmodifiableList(fieldAccesses);
    }

    List<FieldAccess> collectChainedFieldAccesses(CompilationController controller) {
        List<FieldAccess> fieldAccesses = new ArrayList<>();
        Types types = controller.getTypes();
        TypeMirror type = getTypeInContext(controller);
        if (type == null) {
            return Collections.emptyList();
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return Collections.emptyList();
        }
        List<VariableElement> fields = getPublicStaticFieldsInClassAndSuperclasses(typeElement, controller);
        fields = getFieldsOrEnumConstantsByAbbreviation(fields);
        fields.forEach(field -> fieldAccesses.add(new FieldAccess(null, field)));
        Collections.sort(fieldAccesses);
        return Collections.unmodifiableList(fieldAccesses);
    }

    private List<VariableElement> getPublicStaticFieldsInClassAndSuperclasses(
            Element element, CompilationController controller) {
        List<VariableElement> fields = new ArrayList<>();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        TypeMirror typeMirror = element.asType();
        Iterable<? extends Element> members;
        try {
            members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                return !elements.isDeprecated(e)
                        && e.getKind() == ElementKind.FIELD
                        && e.getModifiers().contains(Modifier.PUBLIC)
                        && e.getModifiers().contains(Modifier.STATIC);
            });
        } catch (AssertionError error) {
            return Collections.emptyList();
        }
        members.forEach(member -> fields.add((VariableElement) member));
        return Collections.unmodifiableList(fields);
    }

    List<FieldAccess> collectChainedEnumConstantAccesses(CompilationController controller) {
        List<FieldAccess> result = new ArrayList<>();
        Types types = controller.getTypes();
        TypeMirror type = getTypeInContext(controller);
        if (type == null) {
            return Collections.emptyList();
        }
        Element typeElement = types.asElement(type);
        if (typeElement == null) {
            return Collections.emptyList();
        }
        List<VariableElement> enumConstants = getEnumConstants(typeElement, controller);
        enumConstants = getFieldsOrEnumConstantsByAbbreviation(enumConstants);
        enumConstants.forEach(enumConstant -> result.add(new FieldAccess(null, enumConstant)));
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    private List<VariableElement> getEnumConstants(Element element, CompilationController controller) {
        List<VariableElement> enumConstants = new ArrayList<>();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
        TypeMirror typeMirror = element.asType();
        Iterable<? extends Element> members;
        try {
            members = elementUtilities.getMembers(typeMirror, (e, t) -> {
                return !elements.isDeprecated(e) && e.getKind() == ElementKind.ENUM_CONSTANT;
            });
        } catch (AssertionError error) {
            return Collections.emptyList();
        }
        members.forEach(member -> enumConstants.add((VariableElement) member));
        return Collections.unmodifiableList(enumConstants);
    }

    private List<VariableElement> getFieldsOrEnumConstantsByAbbreviation(List<VariableElement> fieldsOrEnumConstants) {
        List<VariableElement> result = new ArrayList<>();
        fieldsOrEnumConstants.forEach(fieldOrEnumConstant -> {
            String fieldOrEnumConstantAbbreviation =
                    StringUtilities.getElementAbbreviation(fieldOrEnumConstant.getSimpleName().toString());
            if (fieldOrEnumConstantAbbreviation.equals(abbreviation.getName())) {
                result.add(fieldOrEnumConstant);
            }
        });
        return Collections.unmodifiableList(result);
    }

    List<Type> collectTypes(CompilationController controller) {
        List<TypeElement> types = new ArrayList<>();
        types.addAll(collectTypesByAbbreviation(controller));
        return types.stream()
                .filter(distinctByKey(element -> element.getSimpleName().toString()))
                .map(type -> new Type(type))
                .sorted()
                .collect(Collectors.toList());
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    boolean afterThis(CompilationController controller) {
        TokenHierarchy<?> tokenHierarchy = controller.getTokenHierarchy();
        TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
        tokenSequence.move(abbreviation.getStartOffset());
        tokenSequence.movePrevious();
        tokenSequence.movePrevious();
        Token<?> token = tokenSequence.token();
        if (token != null) {
            TokenId tokenId = token.id();
            if (tokenId == JavaTokenId.THIS) {
                return true;
            }
        }
        return false;
    }

    List<LocalElement> collectFields(CompilationController controller) {
        List<LocalElement> result = new ArrayList<>();
        List<Element> fields = new ArrayList<>();
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        ElementUtilities elementUtilities = controller.getElementUtilities();
        Elements elements = controller.getElements();
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
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    private List<CodeFragment> insertAssertStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertAssertStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertAssertStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertAssertStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        AssertTree assertTree = make.Assert(make.Literal(true), make.Literal("")); //NOI18N
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, assertTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(assertTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertAssertStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        AssertTree assertTree = make.Assert(make.Literal(true), make.Literal("")); //NOI18N
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, assertTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(assertTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertBreakStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.BLOCK && currentTree.getKind() != Tree.Kind.SWITCH) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                BreakTree breakTree = make.Break(null);
                int insertIndex;
                switch (currentTree.getKind()) {
                    case BLOCK:
                        BlockTree currentBlock = (BlockTree) currentTree;
                        insertIndex = findInsertIndexInBlock(currentBlock, copy);
                        if (insertIndex == -1) {
                            return;
                        }
                        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, breakTree);
                        copy.rewrite(currentBlock, newBlock);
                        statements.add(new Statement(breakTree.toString()));
                        break;
                    case SWITCH:
                        SwitchTree currentSwitch = (SwitchTree) currentTree;
                        insertIndex = findInsertIndexInSwitchStatement(currentSwitch, copy);
                        if (insertIndex == -1) {
                            return;
                        }
                        List<? extends CaseTree> cases = currentSwitch.getCases();
                        CaseTree currentCaseTree = cases.get(insertIndex - 1);
                        CaseTree newCaseTree = make.insertCaseStatement(
                                currentCaseTree,
                                currentCaseTree.getStatements().size(),
                                breakTree);
                        copy.rewrite(currentCaseTree, newCaseTree);
                        statements.add(new Statement(breakTree.toString()));
                        break;
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertCaseStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.SWITCH) {
                    return;
                }
                SwitchTree currentSwitch = (SwitchTree) currentTree;
                int insertIndex = findInsertIndexInSwitchStatement(currentSwitch, copy);
                if (insertIndex == -1) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                CaseTree newCase = make.Case(make.Identifier(""), Collections.singletonList(make.Break(null))); //NOI18N
                SwitchTree newSwitch = make.insertSwitchCase(currentSwitch, insertIndex, newCase);
                copy.rewrite(currentSwitch, newSwitch);
                statements.add(new Statement(newCase.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertClassDeclaration() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                int insertIndex;
                TreeMaker make = copy.getTreeMaker();
                ClassTree classTree =
                        make.Class(
                                make.Modifiers(Collections.emptySet()),
                                "Class", //NOI18N
                                Collections.emptyList(),
                                null,
                                Collections.emptyList(),
                                Collections.emptyList());
                Tree currentTree = currentPath.getLeaf();
                switch (currentTree.getKind()) {
                    case BLOCK:
                        BlockTree currentBlockTree = (BlockTree) currentTree;
                        insertIndex = findInsertIndexInBlock(currentBlockTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        BlockTree newBlockTree =
                                make.insertBlockStatement(currentBlockTree, insertIndex, classTree);
                        copy.rewrite(currentBlockTree, newBlockTree);
                        statements.add(new Statement(classTree.toString()));
                        break;
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree currentClassEnumOrInterfaceTree = (ClassTree) currentTree;
                        insertIndex = findInsertIndexInClassEnumOrInterface(currentClassEnumOrInterfaceTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        ClassTree newClassEnumOrInterfaceTree =
                                make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, classTree);
                        copy.rewrite(currentClassEnumOrInterfaceTree, newClassEnumOrInterfaceTree);
                        statements.add(new Statement(classTree.toString()));
                        break;
                    case COMPILATION_UNIT:
                        CompilationUnitTree currentCompilationUnitTree = (CompilationUnitTree) currentTree;
                        insertIndex = findInsertIndexInCompilationUnit(currentCompilationUnitTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        CompilationUnitTree newCompilationUnitTree =
                                make.insertCompUnitTypeDecl(currentCompilationUnitTree, insertIndex, classTree);
                        copy.rewrite(currentCompilationUnitTree, newCompilationUnitTree);
                        statements.add(new Statement(classTree.toString()));
                        break;
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertContinueStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.BLOCK) {
                    return;
                }
                int insertIndex;
                BlockTree currentBlock = (BlockTree) currentTree;
                insertIndex = findInsertIndexInBlock(currentBlock, copy);
                if (insertIndex == -1) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                ContinueTree continueTree = make.Continue(null);
                BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, continueTree);
                copy.rewrite(currentBlock, newBlock);
                statements.add(new Statement(continueTree.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertDoWhileStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertDoWhileStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertDoWhileStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertDoWhileStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        DoWhileLoopTree doWhileLoopTree =
                make.DoWhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, doWhileLoopTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(doWhileLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertDoWhileStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        DoWhileLoopTree doWhileLoopTree =
                make.DoWhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, doWhileLoopTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(doWhileLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertEnumDeclaration() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                int insertIndex;
                TreeMaker make = copy.getTreeMaker();
                ClassTree enumTree =
                        make.Enum(
                                make.Modifiers(Collections.emptySet()),
                                "Enum", //NOI18N
                                Collections.emptyList(),
                                Collections.emptyList());
                Tree currentTree = currentPath.getLeaf();
                switch (currentTree.getKind()) {
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree currentClassEnumOrInterfaceTree = (ClassTree) currentTree;
                        insertIndex = findInsertIndexInClassEnumOrInterface(currentClassEnumOrInterfaceTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        ClassTree newClassEnumOrInterfaceTree =
                                make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, enumTree);
                        copy.rewrite(currentClassEnumOrInterfaceTree, newClassEnumOrInterfaceTree);
                        statements.add(new Statement(enumTree.toString()));
                        break;
                    case COMPILATION_UNIT:
                        CompilationUnitTree currentCompilationUnitTree = (CompilationUnitTree) currentTree;
                        insertIndex = findInsertIndexInCompilationUnit(currentCompilationUnitTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        CompilationUnitTree newCompilationUnitTree =
                                make.insertCompUnitTypeDecl(currentCompilationUnitTree, insertIndex, enumTree);
                        copy.rewrite(currentCompilationUnitTree, newCompilationUnitTree);
                        statements.add(new Statement(enumTree.toString()));
                        break;
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertExtendsTree() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.CLASS && currentTree.getKind() != Tree.Kind.INTERFACE) {
                    return;
                }
                ClassTree currentClassOrInterfaceTree = (ClassTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                IdentifierTree extendsIdentifier = make.Identifier(""); //NOI18N
                ClassTree newClassOrInterfaceTree = make.Class(
                        currentClassOrInterfaceTree.getModifiers(),
                        currentClassOrInterfaceTree.getSimpleName(),
                        currentClassOrInterfaceTree.getTypeParameters(),
                        extendsIdentifier,
                        currentClassOrInterfaceTree.getImplementsClause(),
                        currentClassOrInterfaceTree.getMembers());
                copy.rewrite(currentClassOrInterfaceTree, newClassOrInterfaceTree);
                statements.add(new Statement(extendsIdentifier.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInSwitchStatement(SwitchTree switchTree, CompilationController controller) {
        AtomicInteger insertIndex = new AtomicInteger(-1);
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends CaseTree> cases = switchTree.getCases();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = cases.size();
        switch (size) {
            case 0: {
                insertIndex.set(0);
                break;
            }
            case 1: {
                CaseTree currentCase = cases.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentCase);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    insertIndex.set(0);
                } else {
                    insertIndex.set(1);
                }
                break;
            }
            case 2: {
                CaseTree previousCase = cases.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousCase);
                CaseTree currentCase = cases.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentCase);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    insertIndex.set(0);
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    insertIndex.set(size);
                } else {
                    insertIndex.set(1);
                }
                break;
            }
            default: {
                for (int i = 1; i < size; i++) {
                    CaseTree previousCase = cases.get(i - 1);
                    long previousStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, previousCase);
                    CaseTree currentCase = cases.get(i);
                    long currentStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, currentCase);
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
        return insertIndex.get();
    }

    private List<CodeFragment> insertIfStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertIfStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertIfStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertIfStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        IfTree ifStatement = make.If(make.Identifier("true"), make.Block(Collections.emptyList(), false), null); //NOI18N
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, ifStatement);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(ifStatement.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertIfStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        IfTree ifStatement = make.If(make.Identifier("true"), make.Block(Collections.emptyList(), false), null); //NOI18N
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, ifStatement);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(ifStatement.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertImplementsTree() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.CLASS && currentTree.getKind() != Tree.Kind.ENUM) {
                    return;
                }
                ClassTree currentClassOrEnum = (ClassTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                IdentifierTree implementsIdentifier = make.Identifier(""); //NOI18N
                ClassTree newClassOrEnum = make.addClassImplementsClause(currentClassOrEnum, implementsIdentifier);
                copy.rewrite(currentClassOrEnum, newClassOrEnum);
                statements.add(new Statement(implementsIdentifier.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertImportStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.COMPILATION_UNIT) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                ImportTree importTree = make.Import(make.Identifier(""), false); //NOI18N
                CompilationUnitTree currentCompilationUnitTree = (CompilationUnitTree) currentTree;
                int insertIndex = findInsertIndexInImportTree(copy);
                if (insertIndex == -1) {
                    return;
                }
                CompilationUnitTree newCompilationUnitTree =
                        make.insertCompUnitImport(currentCompilationUnitTree, insertIndex, importTree);
                copy.rewrite(currentCompilationUnitTree, newCompilationUnitTree);
                statements.add(new Statement(importTree.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInImportTree(CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends ImportTree> imports = compilationUnit.getImports();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = imports.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                ImportTree currentImport = imports.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentImport);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                ImportTree previousImport = imports.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousImport);
                ImportTree currentImport = imports.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentImport);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
                }
            }
            default: {
                for (int i = 1; i < size; i++) {
                    ImportTree previousImport = imports.get(i - 1);
                    long previousStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, previousImport);
                    ImportTree currentImport = imports.get(i);
                    long currentStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, currentImport);
                    if (i < size - 1) {
                        if (abbreviation.getStartOffset() < previousStartPosition) {
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private List<CodeFragment> insertInterfaceDeclaration() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                int insertIndex;
                TreeMaker make = copy.getTreeMaker();
                ClassTree interfaceTree =
                        make.Interface(
                                make.Modifiers(Collections.emptySet()),
                                "Interface", //NOI18N
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList());
                Tree currentTree = currentPath.getLeaf();
                switch (currentTree.getKind()) {
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree currentClassEnumOrInterfaceTree = (ClassTree) currentTree;
                        insertIndex = findInsertIndexInClassEnumOrInterface(currentClassEnumOrInterfaceTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        ClassTree newClassEnumOrInterfaceTree =
                                make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, interfaceTree);
                        copy.rewrite(currentClassEnumOrInterfaceTree, newClassEnumOrInterfaceTree);
                        statements.add(new Statement(interfaceTree.toString()));
                        break;
                    case COMPILATION_UNIT:
                        CompilationUnitTree currentCompilationUnitTree = (CompilationUnitTree) currentTree;
                        insertIndex = findInsertIndexInCompilationUnit(currentCompilationUnitTree, copy);
                        if (insertIndex == -1) {
                            break;
                        }
                        CompilationUnitTree newCompilationUnitTree =
                                make.insertCompUnitTypeDecl(currentCompilationUnitTree, insertIndex, interfaceTree);
                        copy.rewrite(currentCompilationUnitTree, newCompilationUnitTree);
                        statements.add(new Statement(interfaceTree.toString()));
                        break;
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInClassEnumOrInterface(
            ClassTree classEnumOrInterfaceTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends Tree> members = classEnumOrInterfaceTree.getMembers();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = members.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                Tree currentMember = members.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentMember);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                Tree previousMember = members.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousMember);
                Tree currentMember = members.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentMember);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
                }
            }
            default: {
                for (int i = 1; i < size; i++) {
                    Tree previousMember = members.get(i - 1);
                    long previousStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, previousMember);
                    Tree currentMember = members.get(i);
                    long currentStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, currentMember);
                    if (i < size - 1) {
                        if (abbreviation.getStartOffset() < previousStartPosition) {
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private int findInsertIndexInCompilationUnit(
            CompilationUnitTree compilationUnitTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends Tree> typeDecls = compilationUnitTree.getTypeDecls();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = typeDecls.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                Tree currentTypeDecl = typeDecls.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentTypeDecl);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                Tree previousTypeDecl = typeDecls.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousTypeDecl);
                Tree currentTypeDecl = typeDecls.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentTypeDecl);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
                }
            }
            default: {
                for (int i = 1; i < size; i++) {
                    Tree previousTypeDecl = typeDecls.get(i - 1);
                    long previousStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, previousTypeDecl);
                    Tree currentTypeDecl = typeDecls.get(i);
                    long currentStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, currentTypeDecl);
                    if (i < size - 1) {
                        if (abbreviation.getStartOffset() < previousStartPosition) {
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return i - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private List<CodeFragment> insertReturnStatement() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                String returnVar = returnVar(copy);
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
                int insertIndex = findInsertIndexInBlock(currentBlock, copy);
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

    private List<CodeFragment> insertSwitchStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertSwitchStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertSwitchStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertSwitchStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        SwitchTree switchTree =
                make.Switch(
                        make.Identifier(""), //NOI18N
                        Collections.singletonList(
                                make.Case(make.Identifier(""), Collections.singletonList(make.Break(null))))); //NOI18N
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, switchTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(switchTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertSwitchStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        SwitchTree switchTree =
                make.Switch(
                        make.Identifier(""), //NOI18N
                        Collections.singletonList(
                                make.Case(make.Identifier(""), Collections.singletonList(make.Break(null))))); //NOI18N
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, switchTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(switchTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertTryStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertTryStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertTryStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertTryStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        TryTree tryTree =
                make.Try(
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
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, tryTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(tryTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertTryStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        TryTree tryTree =
                make.Try(
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
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, tryTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(tryTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertThrowStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertThrowStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertThrowStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertThrowStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        ThrowTree throwTree = make.Throw(make.Identifier("new IllegalArgumentException()")); //NOI18N
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, throwTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(throwTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertThrowStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        ThrowTree throwTree = make.Throw(make.Identifier("new IllegalArgumentException()")); //NOI18N
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, throwTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(throwTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertWhileStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertWhileStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertWhileStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertWhileStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        WhileLoopTree whileLoopTree = make.WhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, whileLoopTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(whileLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertWhileStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        WhileLoopTree whileLoopTree = make.WhileLoop(make.Literal(true), make.Block(Collections.emptyList(), false));
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, whileLoopTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(whileLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInBlock(BlockTree blockTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends StatementTree> statements = blockTree.getStatements();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = statements.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                StatementTree currentStatement = statements.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                StatementTree previousStatement = statements.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousStatement);
                StatementTree currentStatement = statements.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
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
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private String returnVar(CompilationController controller) {
        String methodType = owningMethodType(controller);
        if (methodType == null) {
            return null;
        }
        VariableElement variable = instanceOf(methodType, "", controller); //NOI18N
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

    private String owningMethodType(CompilationController controller) {
        TreeUtilities treeUtilities = controller.getTreeUtilities();
        Trees trees = controller.getTrees();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        TreePath methodOrLambdaPath = treeUtilities.getPathElementOfKind(
                EnumSet.of(Tree.Kind.LAMBDA_EXPRESSION, Tree.Kind.METHOD), currentPath);
        if (methodOrLambdaPath == null) {
            return null;
        }
        Tree methodOrLambda = methodOrLambdaPath.getLeaf();
        if (methodOrLambda.getKind() == Tree.Kind.METHOD) {
            ExecutableElement method = (ExecutableElement) trees.getElement(methodOrLambdaPath);
            TypeMirror returnType = method.getReturnType();
            if (returnType != null) {
                return returnType.toString();
            }
        }
        return null;
    }

    public ExpressionTree createMethodInvocationWithoutReturnValue(MethodInvocation methodInvocation) {
        AtomicReference<ExpressionTree> expression = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()), methodInvocation.getArguments());
                if (methodInvocation.getScope() == null) {
                    expression.set(methodInvocationTree);
                } else {
                    expression.set(make.MemberSelect(
                            TypeElement.class
                                    .isInstance(methodInvocation.getScope())
                            ? make.QualIdent(methodInvocation.getScope().toString())
                            : make.Identifier(methodInvocation.getScope()),
                            methodInvocationTree.toString()));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return expression.get();
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
                moveStateToParsedPhase(copy);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodInvocation.getMethod()), methodInvocation.getArguments());
                if (methodInvocation.getScope() == null) {
                    expressionStatement.set(make.ExpressionStatement(methodInvocationTree));
                } else {
                    Element scope = methodInvocation.getScope();
                    if (TypeElement.class.isInstance(scope)) {
                        expressionStatement.set(make.ExpressionStatement(make.MemberSelect(
                                make.QualIdent(scope), methodInvocationTree.toString())));
                    } else {
                        expressionStatement.set(make.ExpressionStatement(make.MemberSelect(
                                make.Identifier(scope), methodInvocationTree.toString())));
                    }
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
                moveStateToParsedPhase(copy);
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
                        initializer = make.MemberSelect(
                                make.QualIdent(methodInvocation.getScope()), methodInvocationTree.toString());
                    } else {
                        initializer = make.MemberSelect(
                                make.Identifier(methodInvocation.getScope()), methodInvocationTree.toString());
                    }
                }
                Set<String> variableNames = getVariableNames(methodInvocation.getMethod().getReturnType(), copy);
                String variableName = variableNames.isEmpty() ? "" : variableNames.iterator().next(); //NOI18N
                variable.set(make.Variable(modifiers, variableName, type, initializer));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return variable.get();
    }

    private void insertAssignmentTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        AssignmentTree currentTree = (AssignmentTree) getCurrentTreeOfKind(copy, Tree.Kind.ASSIGNMENT);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        String expr = currentTree.getExpression().toString();
        int errorIndex = expr.indexOf("(ERROR)"); //NOI18N
        if (errorIndex >= 0) {
            expr = expr.substring(0, errorIndex)
                    .concat(expression.toString())
                    .concat(expr.substring(errorIndex + 7));
        }
        AssignmentTree newTree = make.Assignment(currentTree.getVariable(), make.Identifier(expr));
        copy.rewrite(currentTree, newTree);
    }

    private Tree getCurrentTreeOfKind(WorkingCopy copy, Tree.Kind kind) {
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return null;
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != kind) {
            return null;
        }
        return currentTree;
    }

    private void insertBinaryTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make, Tree.Kind kind) {
        BinaryTree currentTree = (BinaryTree) getCurrentTreeOfKind(copy, kind);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        BinaryTree newTree = make.Binary(kind, currentTree.getLeftOperand(), expression);
        copy.rewrite(currentTree, newTree);
    }

    public void insertBlockTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        BlockTree currentTree = (BlockTree) getCurrentTreeOfKind(copy, Tree.Kind.BLOCK);
        if (currentTree == null) {
            return;
        }
        TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
        sequence.move(abbreviation.getStartOffset());
        moveToNextNonWhitespaceToken(sequence);
        moveToNextNonWhitespaceToken(sequence);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath path = treeUtilities.getPathElementOfKind(
                EnumSet.of(Tree.Kind.BLOCK, Tree.Kind.CLASS, Tree.Kind.VARIABLE),
                treeUtilities.pathFor(sequence.offset()));
        Supplier<Void> insertStatementInBlock = () -> {
            int insertIndex = findInsertIndexInBlock(currentTree, copy);
            if (insertIndex == -1) {
                return null;
            }
            BlockTree newTree;
            VariableTree variable;
            TypeMirror type = null;
            LiteralTree initializer = null;
            Types types = copy.getTypes();
            switch (fragment.getKind()) {
                case METHOD_INVOCATION:
                    MethodInvocation invocation = (MethodInvocation) fragment;
                    if (isMethodReturnVoid(invocation.getMethod())) {
                        ExpressionStatementTree methodInvocation = createVoidMethodInvocation(invocation);
                        newTree = make.insertBlockStatement(currentTree, insertIndex, methodInvocation);
                    } else {
                        VariableTree methodInvocation = createMethodInvocationWithReturnValue(invocation);
                        newTree = make.insertBlockStatement(currentTree, insertIndex, methodInvocation);
                    }
                    copy.rewrite(currentTree, newTree);
                    break;
                case KEYWORD:
                    newTree = make.insertBlockStatement(
                            currentTree,
                            insertIndex,
                            make.ExpressionStatement(make.Identifier(fragment.toString())));
                    copy.rewrite(currentTree, newTree);
                    break;
                case PRIMITIVE_TYPE:
                    switch (fragment.toString()) {
                        case "char": //NOI18N
                            initializer = make.Literal('\0');
                            type = types.getPrimitiveType(TypeKind.CHAR);
                            break;
                        case "boolean": //NOI18N
                            initializer = make.Literal(true);
                            type = types.getPrimitiveType(TypeKind.BOOLEAN);
                            break;
                        case "byte": //NOI18N
                            initializer = make.Literal(0);
                            type = types.getPrimitiveType(TypeKind.BYTE);
                            break;
                        case "int": //NOI18N
                            initializer = make.Literal(0);
                            type = types.getPrimitiveType(TypeKind.INT);
                            break;
                        case "short": //NOI18N
                            initializer = make.Literal(0);
                            type = types.getPrimitiveType(TypeKind.SHORT);
                            break;
                        case "long": //NOI18N
                            initializer = make.Literal(0L);
                            type = types.getPrimitiveType(TypeKind.LONG);
                            break;
                        case "float": //NOI18N
                            initializer = make.Literal(0.0F);
                            type = types.getPrimitiveType(TypeKind.FLOAT);
                            break;
                        case "double": //NOI18N
                            initializer = make.Literal(0.0);
                            type = types.getPrimitiveType(TypeKind.DOUBLE);
                            break;
                        case "String": //NOI18N
                            initializer = make.Literal(""); //NOI18N
                            type = types.getDeclaredType(copy.getElements().getTypeElement("java.lang.String")); //NOI18N
                            break;
                    }
                    variable =
                            make.Variable(
                                    make.Modifiers(Collections.emptySet()),
                                    getVariableNames(type, copy).iterator().next(),
                                    make.Identifier(fragment.toString()),
                                    initializer);
                    newTree = make.insertBlockStatement(currentTree, insertIndex, variable);
                    copy.rewrite(currentTree, newTree);
                    break;
                case TYPE:
                    type = types.getDeclaredType(((Type) fragment).getType());
                    initializer = make.Literal(null);
                    variable =
                            make.Variable(
                                    make.Modifiers(Collections.emptySet()),
                                    getVariableNames(type, copy).iterator().next(),
                                    make.QualIdent(fragment.toString()),
                                    initializer);
                    newTree = make.insertBlockStatement(currentTree, insertIndex, variable);
                    copy.rewrite(currentTree, newTree);
                    break;
            }
            return null;
        };
        if (path != null) {
            switch (path.getLeaf().getKind()) {
                case CLASS:
                    ClassTree classTree = (ClassTree) path.getLeaf();
                    int[] classBodySpan = treeUtilities.findBodySpan(classTree);
                    SourcePositions sourcePositions = copy.getTrees().getSourcePositions();
                    long blockStartPosition = sourcePositions.getStartPosition(copy.getCompilationUnit(), currentTree);
                    if (blockStartPosition < classBodySpan[0]) {
                        ModifiersTree modifiers = classTree.getModifiers();
                        ExpressionTree expression = getExpressionToInsert(fragment, make);
                        ModifiersTree newModifiers =
                                make.addModifiersModifier(modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                        copy.rewrite(modifiers, newModifiers);
                    } else {
                        insertStatementInBlock.get();
                    }
                    break;
                case VARIABLE:
                    VariableTree variable = (VariableTree) path.getLeaf();
                    ModifiersTree modifiers = variable.getModifiers();
                    ExpressionTree expression = getExpressionToInsert(fragment, make);
                    ModifiersTree newModifiers =
                            make.addModifiersModifier(modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                    copy.rewrite(modifiers, newModifiers);
                    break;
                default:
                    insertStatementInBlock.get();
            }
        }
    }

    private void insertCaseTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        CaseTree currentTree = (CaseTree) getCurrentTreeOfKind(copy, Tree.Kind.CASE);
        if (currentTree == null) {
            return;
        }
        TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
        sequence.move(abbreviation.getStartOffset());
        while (sequence.movePrevious() && sequence.token().id() == JavaTokenId.WHITESPACE) {
        }
        CaseTree newTree;
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        if (sequence.token().id() != JavaTokenId.CASE) {
            int insertIndex = findInsertIndexInCaseTree(currentTree, copy);
            newTree = make.insertCaseStatement(currentTree, insertIndex, make.ExpressionStatement(expression));
        } else {
            newTree = make.Case(expression, currentTree.getStatements());
        }
        copy.rewrite(currentTree, newTree);
    }

    private int findInsertIndexInCaseTree(CaseTree caseTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends StatementTree> statements = caseTree.getStatements();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = statements.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                StatementTree currentStatement = statements.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                StatementTree previousStatement = statements.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousStatement);
                StatementTree currentStatement = statements.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
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
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private List<CodeFragment> insertCatchTree() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.TRY) {
                    return;
                }
                TryTree currentTry = (TryTree) currentTree;
                int insertIndex = findInsertIndexInTryStatement(currentTry, copy);
                if (insertIndex == -1) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                CatchTree catchTree =
                        make.Catch(
                                make.Variable(
                                        make.Modifiers(Collections.emptySet()),
                                        "e", //NOI18N
                                        make.Identifier("Exception"), //NOI18N
                                        null),
                                make.Block(Collections.emptyList(), false));
                TryTree newTry = make.insertTryCatch(currentTry, insertIndex, catchTree);
                copy.rewrite(currentTry, newTry);
                statements.add(new Statement(catchTree.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private int findInsertIndexInTryStatement(TryTree tryTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends CatchTree> catches = tryTree.getCatches();
        SourcePositions sourcePositions = trees.getSourcePositions();
        int size = catches.size();
        switch (size) {
            case 0: {
                return 0;
            }
            case 1: {
                CatchTree currentCatch = catches.get(0);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentCatch);
                if (abbreviation.getStartOffset() < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                CatchTree previousCatch = catches.get(0);
                long previousStartPosition =
                        sourcePositions.getStartPosition(compilationUnit, previousCatch);
                CatchTree currentCatch = catches.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentCatch);
                if (abbreviation.getStartOffset() < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < abbreviation.getStartOffset()) {
                    return size;
                } else {
                    return 1;
                }
            }
            default: {
                for (int i = 1; i < size; i++) {
                    CatchTree previousCatch = catches.get(i - 1);
                    long previousStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, previousCatch);
                    CatchTree currentCatch = catches.get(i);
                    long currentStartPosition =
                            sourcePositions.getStartPosition(compilationUnit, currentCatch);
                    if (i < size - 1) {
                        if (abbreviation.getStartOffset() < previousStartPosition) {
                            return i - 1;
                        } else if (previousStartPosition < abbreviation.getStartOffset()
                                && abbreviation.getStartOffset() < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (abbreviation.getStartOffset() < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    private void insertClassEnumOrInterfaceTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        TreePath currentPath = copy.getTreeUtilities().pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return;
        }
        ClassTree currentClassEnumOrInterfaceTree = (ClassTree) currentPath.getLeaf();
        int insertIndex = findInsertIndexInClassEnumOrInterface(currentClassEnumOrInterfaceTree, copy);
        if (insertIndex == -1) {
            return;
        }
        Types types = copy.getTypes();
        TypeMirror type = null;
        VariableTree variable;
        ClassTree newClassEnumOrInterfaceTree;
        ExpressionTree returnValue = null;
        MethodTree method;
        switch (fragment.getKind()) {
            case KEYWORD:
                if (fragment.toString().equals("void")) { //NOI18N
                    if (currentPath.getLeaf().getKind() != Tree.Kind.CLASS
                            && currentPath.getLeaf().getKind() != Tree.Kind.ENUM
                            && currentPath.getLeaf().getKind() != Tree.Kind.INTERFACE) {
                        return;
                    }
                    Tree classEnumOrInterfaceTree = currentPath.getLeaf();
                    switch (classEnumOrInterfaceTree.getKind()) {
                        case CLASS:
                        case ENUM:
                            ClassTree classOrEnumTree = (ClassTree) classEnumOrInterfaceTree;
                            insertIndex = findInsertIndexInClassEnumOrInterface(classOrEnumTree, copy);
                            method =
                                    make.Method(
                                            make.Modifiers(Collections.emptySet()),
                                            "method", //NOI18N
                                            make.PrimitiveType(TypeKind.VOID),
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            make.Block(Collections.emptyList(), false),
                                            null);
                            ClassTree newClassOrEnumTree = make.insertClassMember(classOrEnumTree, insertIndex, method);
                            copy.rewrite(classEnumOrInterfaceTree, newClassOrEnumTree);
                            break;
                        case INTERFACE:
                            ClassTree interfaceTree = (ClassTree) classEnumOrInterfaceTree;
                            insertIndex = findInsertIndexInClassEnumOrInterface(interfaceTree, copy);
                            IdentifierTree methodTree = make.Identifier("void method();"); //NOI18N
                            ClassTree newInterfaceTree = make.insertClassMember(interfaceTree, insertIndex, methodTree);
                            copy.rewrite(classEnumOrInterfaceTree, newInterfaceTree);
                            break;
                    }
                    break;
                }
                break;
            case PRIMITIVE_TYPE:
                switch (fragment.toString()) {
                    case "char": //NOI18N
                        returnValue = make.Literal('\0');
                        type = types.getPrimitiveType(TypeKind.CHAR);
                        break;
                    case "boolean": //NOI18N
                        returnValue = make.Literal(true);
                        type = types.getPrimitiveType(TypeKind.BOOLEAN);
                        break;
                    case "byte": //NOI18N
                        returnValue = make.Literal(0);
                        type = types.getPrimitiveType(TypeKind.BYTE);
                        break;
                    case "int": //NOI18N
                        returnValue = make.Literal(0);
                        type = types.getPrimitiveType(TypeKind.INT);
                        break;
                    case "short": //NOI18N
                        returnValue = make.Literal(0);
                        type = types.getPrimitiveType(TypeKind.SHORT);
                        break;
                    case "long": //NOI18N
                        returnValue = make.Literal(0L);
                        type = types.getPrimitiveType(TypeKind.LONG);
                        break;
                    case "float": //NOI18N
                        returnValue = make.Literal(0.0F);
                        type = types.getPrimitiveType(TypeKind.FLOAT);
                        break;
                    case "double": //NOI18N
                        returnValue = make.Literal(0.0);
                        type = types.getPrimitiveType(TypeKind.DOUBLE);
                        break;
                    case "String": //NOI18N
                        returnValue = make.Literal(""); //NOI18N
                        type = types.getDeclaredType(copy.getElements().getTypeElement("java.lang.String")); //NOI18N
                        break;
                }
                if (!inMethodSection(currentClassEnumOrInterfaceTree, copy)) {
                    variable =
                            make.Variable(
                                    make.Modifiers(Collections.singleton(Modifier.PRIVATE)),
                                    getVariableNames(type, copy).iterator().next(),
                                    make.Identifier(fragment.toString()),
                                    null);
                    newClassEnumOrInterfaceTree =
                            make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, variable);
                } else {
                    method =
                            make.Method(
                                    make.Modifiers(Collections.emptySet()),
                                    "method", //NOI18N
                                    make.Identifier(fragment.toString()),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    make.Block(Collections.singletonList(make.Return(returnValue)), false),
                                    null);
                    newClassEnumOrInterfaceTree =
                            make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, method);
                }
                copy.rewrite(currentClassEnumOrInterfaceTree, newClassEnumOrInterfaceTree);
                break;
            case TYPE:
                if (!inMethodSection(currentClassEnumOrInterfaceTree, copy)) {
                    type = types.getDeclaredType(((Type) fragment).getType());
                    variable =
                            make.Variable(
                                    make.Modifiers(Collections.singleton(Modifier.PRIVATE)),
                                    getVariableNames(type, copy).iterator().next(),
                                    make.QualIdent(fragment.toString()),
                                    null);
                    newClassEnumOrInterfaceTree =
                            make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, variable);
                } else {
                    method =
                            make.Method(
                                    make.Modifiers(Collections.emptySet()),
                                    "method", //NOI18N
                                    make.QualIdent(fragment.toString()),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    Collections.emptyList(),
                                    make.Block(Collections.singletonList(make.Return(make.Literal(null))), false),
                                    null);
                    newClassEnumOrInterfaceTree =
                            make.insertClassMember(currentClassEnumOrInterfaceTree, insertIndex, method);
                }
                copy.rewrite(currentClassEnumOrInterfaceTree, newClassEnumOrInterfaceTree);
                break;
            default:
                TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
                sequence.move(abbreviation.getStartOffset());
                moveToNextNonWhitespaceToken(sequence);
                moveToNextNonWhitespaceToken(sequence);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath path = treeUtilities.getPathElementOfKind(EnumSet.of(Tree.Kind.METHOD, Tree.Kind.CLASS,
                        Tree.Kind.INTERFACE, Tree.Kind.ENUM, Tree.Kind.VARIABLE),
                        treeUtilities.pathFor(sequence.offset()));
                if (path == null) {
                    return;
                }
                ExpressionTree expression = getExpressionToInsert(fragment, make);
                ModifiersTree modifiers;
                ModifiersTree newModifiers;
                switch (path.getLeaf().getKind()) {
                    case METHOD:
                        MethodTree methodTree = (MethodTree) path.getLeaf();
                        modifiers = methodTree.getModifiers();
                        newModifiers = make.addModifiersModifier(
                                modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                        copy.rewrite(modifiers, newModifiers);
                        break;
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        ClassTree classTree = (ClassTree) path.getLeaf();
                        modifiers = classTree.getModifiers();
                        newModifiers = make.addModifiersModifier(
                                modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                        copy.rewrite(modifiers, newModifiers);
                        break;
                    case VARIABLE:
                        VariableTree variableTree = (VariableTree) path.getLeaf();
                        modifiers = variableTree.getModifiers();
                        newModifiers = make.addModifiersModifier(
                                modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                        copy.rewrite(modifiers, newModifiers);
                        break;
                }
        }
    }

    private boolean inMethodSection(ClassTree classInterfaceOrEnumTree, CompilationController controller) {
        Trees trees = controller.getTrees();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends Tree> members = classInterfaceOrEnumTree.getMembers();
        SourcePositions sourcePositions = trees.getSourcePositions();
        if (members.isEmpty()) {
            return false;
        }
        int size = members.size();
        for (int i = 1; i < size; i++) {
            Tree previousMember = members.get(i - 1);
            long previousStartOffset = sourcePositions.getStartPosition(compilationUnit, previousMember);
            Tree currentMember = members.get(i);
            long currentStartOffset = sourcePositions.getStartPosition(compilationUnit, currentMember);
            if (i < size - 1) {
                if (abbreviation.getStartOffset() < previousStartOffset) {
                    return false;
                } else if (previousStartOffset < abbreviation.getStartOffset()
                        && abbreviation.getStartOffset() < currentStartOffset
                        && previousMember.getKind() == Tree.Kind.METHOD
                        && currentMember.getKind() == Tree.Kind.METHOD) {
                    return true;
                }
            } else {
                if (currentStartOffset < abbreviation.getStartOffset()) {
                    return currentMember.getKind() == Tree.Kind.METHOD;
                }
            }
        }
        return false;
    }

    private void insertCompilationUnitTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
        sequence.move(abbreviation.getStartOffset());
        moveToNextNonWhitespaceToken(sequence);
        moveToNextNonWhitespaceToken(sequence);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath path = treeUtilities.getPathElementOfKind(
                EnumSet.of(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM),
                treeUtilities.pathFor(sequence.offset()));
        if (path == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        ClassTree classTree = (ClassTree) path.getLeaf();
        ModifiersTree modifiers = classTree.getModifiers();
        ModifiersTree newModifiers =
                make.addModifiersModifier(modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
        copy.rewrite(modifiers, newModifiers);
    }

    private void insertCompoundAssignmentTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make, Tree.Kind kind) {
        CompoundAssignmentTree currentTree = (CompoundAssignmentTree) getCurrentTreeOfKind(copy, kind);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        CompoundAssignmentTree newTree = make.CompoundAssignment(kind, currentTree.getVariable(), expression);
        copy.rewrite(currentTree, newTree);
    }

    private void insertConditionalExpressionTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        ConditionalExpressionTree currentTree =
                (ConditionalExpressionTree) getCurrentTreeOfKind(copy, Tree.Kind.CONDITIONAL_EXPRESSION);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
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
        ConditionalExpressionTree newTree;
        if (colonFound) {
            if (questionFound) {
                newTree =
                        make.ConditionalExpression(
                                expression,
                                currentTree.getTrueExpression(),
                                currentTree.getFalseExpression());
            } else {
                newTree =
                        make.ConditionalExpression(
                                currentTree.getCondition(),
                                expression,
                                currentTree.getFalseExpression());
            }
        } else {
            newTree =
                    make.ConditionalExpression(
                            currentTree.getCondition(),
                            currentTree.getTrueExpression(),
                            expression);
        }
        copy.rewrite(currentTree, newTree);
    }

    private List<CodeFragment> insertElseTree() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.IF) {
                    return;
                }
                IfTree currentIfTree = (IfTree) currentTree;
                TreeMaker make = copy.getTreeMaker();
                BlockTree elseTree = make.Block(Collections.emptyList(), false);
                IfTree newIfTree = make.If(currentIfTree.getCondition(), currentIfTree.getThenStatement(), elseTree);
                copy.rewrite(currentIfTree, newIfTree);
                statements.add(new Statement(elseTree.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertFinallyTree() {
        List<CodeFragment> statements = new ArrayList<>(1);
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
                if (currentPath == null) {
                    return;
                }
                Tree currentTree = currentPath.getLeaf();
                if (currentTree.getKind() != Tree.Kind.TRY) {
                    return;
                }
                TryTree currentTry = (TryTree) currentTree;
                int insertIndex = findInsertIndexInTryStatement(currentTry, copy);
                if (insertIndex == -1) {
                    return;
                }
                TreeMaker make = copy.getTreeMaker();
                BlockTree finallyBlock = make.Block(Collections.emptyList(), false);
                TryTree newTry =
                        make.Try(currentTry.getBlock(), currentTry.getCatches(), finallyBlock);
                copy.rewrite(currentTry, newTry);
                statements.add(new Statement(finallyBlock.toString()));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertForStatement() {
        List<CodeFragment> fragments = new ArrayList<>();
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToParsedPhase(copy);
                if (isCaseStatement(copy)) {
                    fragments.addAll(insertForStatementInCaseTree(copy));
                } else {
                    fragments.addAll(insertForStatementInBlock(copy));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(fragments);
    }

    private List<CodeFragment> insertForStatementInCaseTree(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.CASE) {
            return Collections.emptyList();
        }
        CaseTree currentCase = (CaseTree) currentTree;
        int insertIndex = findInsertIndexInCaseTree(currentCase, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        ForLoopTree forLoopTree = make.ForLoop(
                Collections.singletonList(make.Variable(
                        make.Modifiers(Collections.emptySet()),
                        "i", //NOI18N
                        make.PrimitiveType(TypeKind.INT),
                        make.Literal(0))),
                make.Binary(Tree.Kind.LESS_THAN, make.Identifier("i"), make.Literal(10)), //NOI18N
                Collections.singletonList(make.ExpressionStatement(
                        make.Unary(Tree.Kind.POSTFIX_INCREMENT, make.Identifier("i")))), //NOI18N
                make.Block(Collections.emptyList(), false)
        );
        CaseTree newCase = make.insertCaseStatement(currentCase, insertIndex, forLoopTree);
        copy.rewrite(currentCase, newCase);
        statements.add(new Statement(forLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private List<CodeFragment> insertForStatementInBlock(WorkingCopy copy) {
        List<CodeFragment> statements = new ArrayList<>(1);
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TreePath currentPath = treeUtilities.pathFor(abbreviation.getStartOffset());
        if (currentPath == null) {
            return Collections.emptyList();
        }
        Tree currentTree = currentPath.getLeaf();
        if (currentTree.getKind() != Tree.Kind.BLOCK) {
            return Collections.emptyList();
        }
        BlockTree currentBlock = (BlockTree) currentTree;
        int insertIndex = findInsertIndexInBlock(currentBlock, copy);
        if (insertIndex == -1) {
            return Collections.emptyList();
        }
        TreeMaker make = copy.getTreeMaker();
        ForLoopTree forLoopTree = make.ForLoop(
                Collections.singletonList(make.Variable(
                        make.Modifiers(Collections.emptySet()),
                        "i", //NOI18N
                        make.PrimitiveType(TypeKind.INT),
                        make.Literal(0))),
                make.Binary(Tree.Kind.LESS_THAN, make.Identifier("i"), make.Literal(10)), //NOI18N
                Collections.singletonList(make.ExpressionStatement(
                        make.Unary(Tree.Kind.POSTFIX_INCREMENT, make.Identifier("i")))), //NOI18N
                make.Block(Collections.emptyList(), false)
        );
        BlockTree newBlock = make.insertBlockStatement(currentBlock, insertIndex, forLoopTree);
        copy.rewrite(currentBlock, newBlock);
        statements.add(new Statement(forLoopTree.toString()));
        return Collections.unmodifiableList(statements);
    }

    private void insertForLoopTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        ForLoopTree currentTree = (ForLoopTree) getCurrentTreeOfKind(copy, Tree.Kind.FOR_LOOP);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
        sequence.move(abbreviation.getStartOffset());
        int semicolonCount = 0;
        while (sequence.movePrevious() && sequence.token().id() != JavaTokenId.FOR) {
            TokenId tokenId = sequence.token().id();
            if (tokenId == JavaTokenId.SEMICOLON) {
                semicolonCount++;
            }
        }
        ForLoopTree newTree;
        switch (semicolonCount) {
            case 0: {
                newTree = make.addForLoopInitializer(currentTree, make.ExpressionStatement(expression));
                break;
            }
            case 1: {
                newTree = make.ForLoop(
                        currentTree.getInitializer(),
                        expression,
                        currentTree.getUpdate(),
                        currentTree.getStatement());
                break;
            }
            default: {
                newTree = make.addForLoopUpdate(currentTree, make.ExpressionStatement(expression));
            }
        }
        copy.rewrite(currentTree, newTree);
    }

    private void insertMemberSelectTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        MemberSelectTree currentTree = (MemberSelectTree) getCurrentTreeOfKind(copy, Tree.Kind.MEMBER_SELECT);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        MemberSelectTree newTree = make.MemberSelect(currentTree.getExpression(), expression.toString());
        SourcePositions sourcePositions = copy.getTrees().getSourcePositions();
        long startPosition = sourcePositions.getStartPosition(copy.getCompilationUnit(), currentTree);
        long endPosition = sourcePositions.getEndPosition(copy.getCompilationUnit(), currentTree);
        long dotCount = currentTree.toString().chars().filter(ch -> ch == '.').count();
        String next = ""; //NOI18N
        if (dotCount > 1) {
            TokenSequence<?> sequence = copy.getTokenHierarchy().tokenSequence();
            sequence.move(abbreviation.getStartOffset());
            while (sequence.moveNext() && sequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            next = Character.toString(sequence.token().text().charAt(0));
        }
        try {
            document.remove((int) startPosition, (int) (endPosition - startPosition));
            if (next.isEmpty()) {
                document.insertString((int) startPosition, newTree.toString(), null);
            } else {
                document.insertString((int) startPosition, newTree.toString() + next, null);
                component.setCaretPosition(component.getCaretPosition() - 1);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void insertMethodTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        MethodTree currentMethodTree = (MethodTree) getCurrentTreeOfKind(copy, Tree.Kind.METHOD);
        if (currentMethodTree == null) {
            return;
        }
        Types types = copy.getTypes();
        MethodTree newMethodTree;
        int insertIndex = findInsertIndexForMethodParameter(currentMethodTree);
        if (insertIndex == -1) {
            return;
        }
        TypeMirror type = null;
        VariableTree variable;
        switch (fragment.getKind()) {
            case MODIFIER:
                ExpressionTree expression = getExpressionToInsert(fragment, make);
                ModifiersTree modifiers = currentMethodTree.getModifiers();
                ModifiersTree newModifiers = make.addModifiersModifier(
                        modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                copy.rewrite(modifiers, newModifiers);
                break;
            case PRIMITIVE_TYPE:
                switch (fragment.toString()) {
                    case "char": //NOI18N
                        type = types.getPrimitiveType(TypeKind.CHAR);
                        break;
                    case "boolean": //NOI18N
                        type = types.getPrimitiveType(TypeKind.BOOLEAN);
                        break;
                    case "byte": //NOI18N
                        type = types.getPrimitiveType(TypeKind.BYTE);
                        break;
                    case "int": //NOI18N
                        type = types.getPrimitiveType(TypeKind.INT);
                        break;
                    case "short": //NOI18N
                        type = types.getPrimitiveType(TypeKind.SHORT);
                        break;
                    case "long": //NOI18N
                        type = types.getPrimitiveType(TypeKind.LONG);
                        break;
                    case "float": //NOI18N
                        type = types.getPrimitiveType(TypeKind.FLOAT);
                        break;
                    case "double": //NOI18N
                        type = types.getPrimitiveType(TypeKind.DOUBLE);
                        break;
                    case "String": //NOI18N
                        type = types.getDeclaredType(copy.getElements().getTypeElement("java.lang.String")); //NOI18N
                        break;
                }
                variable =
                        make.Variable(
                                make.Modifiers(Collections.emptySet()),
                                getVariableNames(type, copy).iterator().next(),
                                make.Identifier(fragment.toString()),
                                null);
                newMethodTree = make.insertMethodParameter(currentMethodTree, insertIndex, variable);
                copy.rewrite(currentMethodTree, newMethodTree);
                break;
            case TYPE:
                type = types.getDeclaredType(((Type) fragment).getType());
                variable =
                        make.Variable(
                                make.Modifiers(Collections.emptySet()),
                                getVariableNames(type, copy).iterator().next(),
                                make.QualIdent(fragment.toString()),
                                null);
                newMethodTree = make.insertMethodParameter(currentMethodTree, insertIndex, variable);
                copy.rewrite(currentMethodTree, newMethodTree);
                break;
        }
    }

    private void insertMethodInvocationTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        MethodInvocationTree currentTree =
                (MethodInvocationTree) getCurrentTreeOfKind(copy, Tree.Kind.METHOD_INVOCATION);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        int insertIndex = findInsertIndexForInvocationArgument(currentTree);
        MethodInvocationTree newTree = make.insertMethodInvocationArgument(currentTree, insertIndex, expression);
        copy.rewrite(currentTree, newTree);
    }

    private int findInsertIndexForInvocationArgument(MethodInvocationTree methodInvocationTree) {
        return findInsertIndexForArgument(methodInvocationTree.getArguments());
    }

    private int findInsertIndexForMethodParameter(MethodTree methodTree) {
        List<? extends VariableTree> parameters = methodTree.getParameters();
        if (parameters.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < parameters.size(); i++) {
            String argument = parameters.get(i).toString();
            if (argument.contains(ConstantDataManager.PARENTHESIZED_ERROR)
                    || argument.contains(ConstantDataManager.ANGLED_ERROR)) {
                return i;
            }
        }
        return -1;
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

    private void insertModifiersTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        ModifiersTree currentTree = (ModifiersTree) getCurrentTreeOfKind(copy, Tree.Kind.MODIFIERS);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        ModifiersTree newTree = make.addModifiersModifier(
                currentTree, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
        copy.rewrite(currentTree, newTree);
    }

    private void insertNewClassTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        NewClassTree currentTree = (NewClassTree) getCurrentTreeOfKind(copy, Tree.Kind.NEW_CLASS);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        int insertIndex = findInsertIndexForInvocationArgument(currentTree);
        NewClassTree newTree = make.insertNewClassArgument(currentTree, insertIndex, expression);
        copy.rewrite(currentTree, newTree);
    }

    private int findInsertIndexForInvocationArgument(NewClassTree newClassTree) {
        return findInsertIndexForArgument(newClassTree.getArguments());
    }

    private void insertParenthesizedTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        ParenthesizedTree currentTree = (ParenthesizedTree) getCurrentTreeOfKind(copy, Tree.Kind.PARENTHESIZED);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        ParenthesizedTree newTree = make.Parenthesized(expression);
        copy.rewrite(currentTree, newTree);
    }

    private void insertReturnTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        ReturnTree currentTree = (ReturnTree) getCurrentTreeOfKind(copy, Tree.Kind.RETURN);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        Tree newTree = make.Return(expression);
        copy.rewrite(currentTree, newTree);
    }

    private void insertUnaryTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make, Tree.Kind kind) {
        UnaryTree currentTree = (UnaryTree) getCurrentTreeOfKind(copy, kind);
        if (currentTree == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        UnaryTree newTree = make.Unary(kind, expression);
        copy.rewrite(currentTree, newTree);
    }

    public void insertVariableTree(CodeFragment fragment, WorkingCopy copy, TreeMaker make) {
        VariableTree currentVariable = (VariableTree) getCurrentTreeOfKind(copy, Tree.Kind.VARIABLE);
        if (currentVariable == null) {
            return;
        }
        ExpressionTree expression = getExpressionToInsert(fragment, make);
        VariableTree newVariable;
        switch (fragment.getKind()) {
            case MODIFIER:
                ModifiersTree modifiers = currentVariable.getModifiers();
                ModifiersTree newModifiers = make.addModifiersModifier(
                        modifiers, Modifier.valueOf(expression.toString().toUpperCase(Locale.getDefault())));
                copy.rewrite(modifiers, newModifiers);
                break;
            case PRIMITIVE_TYPE:
            case TYPE:
                newVariable =
                        make.Variable(
                                currentVariable.getModifiers(),
                                currentVariable.getName(),
                                currentVariable.getType(),
                                make.TypeCast(expression, currentVariable.getInitializer()));
                copy.rewrite(currentVariable, newVariable);
                break;
            default:
                ExpressionTree currentInitializer = currentVariable.getInitializer();
                if (currentInitializer != null) {
                    String initializer = currentInitializer.toString();
                    int errorIndex = initializer.indexOf("(ERROR)"); //NOI18N
                    if (errorIndex >= 0) {
                        initializer = initializer.substring(0, errorIndex)
                                .concat(expression.toString())
                                .concat(initializer.substring(errorIndex + 7));
                        newVariable =
                                make.Variable(
                                        currentVariable.getModifiers(),
                                        currentVariable.getName(),
                                        currentVariable.getType(),
                                        make.Identifier(initializer));
                        copy.rewrite(currentVariable, newVariable);
                    }
                }
        }
    }

    private ExpressionTree getExpressionToInsert(CodeFragment fragment, TreeMaker make) {
        switch (fragment.getKind()) {
            case FIELD_ACCESS:
                FieldAccess fieldAccess = (FieldAccess) fragment;
                if (fieldAccess.getScope() == null) {
                    return make.Identifier(fieldAccess.getName());
                } else {
                    return make.MemberSelect(make.QualIdent(fieldAccess.getScope()), fieldAccess.getName());
                }
            case KEYWORD:
            case LOCAL_ELEMENT:
            case MODIFIER:
            case PRIMITIVE_TYPE:
                return make.Identifier(fragment.toString());
            case TYPE:
                return make.QualIdent(fragment.toString());
            case METHOD_INVOCATION:
                return createMethodInvocationWithoutReturnValue((MethodInvocation) fragment);
            default:
                return null;
        }
    }

    List<Type> collectImportedTypes(CompilationController controller) {
        List<Type> types = new ArrayList<>();
        Elements elements = controller.getElements();
        CompilationUnitTree compilationUnit = controller.getCompilationUnit();
        List<? extends ImportTree> imports = compilationUnit.getImports();
        for (ImportTree importTree : imports) {
            if (importTree.isStatic()) {
                continue;
            }
            String qualifiedIdentifier = importTree.getQualifiedIdentifier().toString();
            if (qualifiedIdentifier.endsWith("*")) { //NOI18N
                continue;
            }
            int lastDotIndex = qualifiedIdentifier.lastIndexOf('.');
            if (lastDotIndex < 0) {
                continue;
            }
            String simpleIdentifier = qualifiedIdentifier.substring(lastDotIndex + 1);
            String typeAbbreviation = StringUtilities.getElementAbbreviation(simpleIdentifier);
            if (typeAbbreviation.equals(abbreviation.getContent())) {
                types.add(new Type(elements.getTypeElement(qualifiedIdentifier)));
            }
        }
        return Collections.unmodifiableList(types);
    }
}
