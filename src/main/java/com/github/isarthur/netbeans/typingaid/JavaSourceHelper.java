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
package com.github.isarthur.netbeans.typingaid;

import com.github.isarthur.netbeans.typingaid.codefragment.FieldAccess;
import com.github.isarthur.netbeans.typingaid.codefragment.Keyword;
import com.github.isarthur.netbeans.typingaid.codefragment.LocalElement;
import com.github.isarthur.netbeans.typingaid.codefragment.MethodCall;
import com.github.isarthur.netbeans.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.typingaid.constants.ConstantDataManager;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceHelper {

    private final JTextComponent component;
    private final Document document;
    private final List<Element> localElements;
    private Types types;
    private int caretPosition;
    private TreeMaker make;
    private WorkingCopy workingCopy;
    private TreeUtilities treeUtilities;
    private Trees trees;
    private CompilationUnitTree compilationUnit;
    private ElementUtilities elementUtilities;
    private Elements elements;
    private String typedAbbreviation;

    public JavaSourceHelper(JTextComponent component) {
        requireNonNull(component, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "component"));
        this.component = component;
        this.document = component.getDocument();
        this.localElements = new ArrayList<>();
        this.caretPosition = -1;
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    private boolean isValid() {
        return caretPosition != -1;
    }

    private void validate() {
        if (!isValid()) {
            throw new IllegalStateException(ConstantDataManager.SHOULD_SET_CARET_POSITION_AND_COLLECT_LOCAL_ELEMENTS);
        }
    }

    void collectLocalElements(int position) {
        if (position < 0 || position >= document.getLength()) {
            throw new IllegalArgumentException(ConstantDataManager.INVALID_POSITION);
        }
        localElements.clear();
        this.caretPosition = position;
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                this.workingCopy = copy;
                types = workingCopy.getTypes();
                trees = workingCopy.getTrees();
                treeUtilities = workingCopy.getTreeUtilities();
                compilationUnit = workingCopy.getCompilationUnit();
                make = workingCopy.getTreeMaker();
                elementUtilities = workingCopy.getElementUtilities();
                elements = workingCopy.getElements();
                Scope scope = treeUtilities.scopeFor(position);
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && (e.getKind() == ElementKind.ENUM_CONSTANT
                                    || e.getKind() == ElementKind.EXCEPTION_PARAMETER
                                    || e.getKind() == ElementKind.FIELD
                                    || e.getKind() == ElementKind.LOCAL_VARIABLE
                                    || e.getKind() == ElementKind.PARAMETER
                                    || e.getKind() == ElementKind.RESOURCE_VARIABLE
                                    || e.getKind() == ElementKind.CLASS
                                    || e.getKind() == ElementKind.INTERFACE
                                    || e.getKind() == ElementKind.ENUM)
                                    && !elements.isDeprecated(e));
                        });
                localMembersAndVars.forEach(localElements::add);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ExpressionStatementTree createVoidMethodCall(MethodCall methodCall) {
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                make.Identifier(methodCall.getMethod()), methodCall.getArguments());
        if (methodCall.getScope() == null) {
            return make.ExpressionStatement(methodInvocationTree);
        }
        return make.ExpressionStatement(
                make.MemberSelect(make.Identifier(methodCall.getScope()), methodInvocationTree.toString()));
    }

    public VariableTree createMethodCallWithReturnValue(MethodCall methodCall) {
        ModifiersTree modifiers = make.Modifiers(Collections.emptySet());
        Tree type = make.Type(methodCall.getMethod().getReturnType());
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(
                Collections.emptyList(),
                make.Identifier(methodCall.getMethod()),
                methodCall.getArguments());
        ExpressionTree initializer;
        if (methodCall.getScope() == null) {
            initializer = methodInvocationTree;
        } else {
            if (isTypeElement(methodCall.getScope())) {
                initializer = make.MemberSelect(make.QualIdent(methodCall.getScope()), methodInvocationTree.toString());
            } else {
                initializer = make.MemberSelect(make.Identifier(methodCall.getScope()), methodInvocationTree.toString());
            }
        }
        String variableName = getVariableName(methodCall, workingCopy);
        VariableTree variableTree = make.Variable(modifiers, variableName, type, initializer);
        return variableTree;
    }

    private String getVariableName(MethodCall methodCall, WorkingCopy workingCopy) {
        Iterator<String> names = Utilities.varNamesSuggestions(methodCall.getMethod().getReturnType(),
                ElementKind.LOCAL_VARIABLE, Collections.emptySet(), null, null, workingCopy.getTypes(),
                workingCopy.getElements(), localElements, CodeStyle.getDefault(document)).iterator();
        if (names.hasNext()) {
            return names.next();
        }
        return "";
    }

    private boolean isTypeElement(Element element) {
        return TypeElement.class.isInstance(element);
    }

    public ExpressionTree createMethodCallWithoutReturnValue(MethodCall methodCall) {
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                make.Identifier(methodCall.getMethod()), methodCall.getArguments());
        if (methodCall.getScope() == null) {
            return methodInvocationTree;
        }
        return make.MemberSelect(make.Identifier(methodCall.getScope()), methodInvocationTree.toString());
    }

    List<Element> getElementsByAbbreviation(String abbreviation) throws IllegalArgumentException {
        validate();
        List<Element> elems = new ArrayList<>();
        localElements.forEach(element -> {
            String elementName = element.getSimpleName().toString();
            String elementAbbreviation = getElementAbbreviation(elementName);
            if (elementAbbreviation.equals(abbreviation)) {
                elems.add(element);
            }
        });
        return Collections.unmodifiableList(elems);
    }

    List<TypeElement> getTypeElementsByAbbreviationInSourceCompileAndBootPath(String abbreviation) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        ClasspathInfo classpathInfo = javaSource.getClasspathInfo();
        ClassIndex classIndex = classpathInfo.getClassIndex();
        Set<ElementHandle<TypeElement>> declaredTypes = classIndex.getDeclaredTypes(
                abbreviation.toUpperCase(),
                ClassIndex.NameKind.CAMEL_CASE,
                EnumSet.of(ClassIndex.SearchScope.SOURCE, ClassIndex.SearchScope.DEPENDENCIES));
        List<TypeElement> typeElements = new ArrayList<>();
        try {
            javaSource.runUserActionTask(compilationController -> {
                moveStateToResolvedPhase(compilationController);
                declaredTypes.forEach(type -> {
                    TypeElement typeElement = type.resolve(compilationController);
                    if (typeElement != null) {
                        String typeName = typeElement.getSimpleName().toString();
                        String typeAbbreviation = getElementAbbreviation(typeName);
                        if (typeAbbreviation.equals(abbreviation)) {
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

    private String getElementAbbreviation(String elementName) {
        if (elementName.isEmpty()) {
            return ConstantDataManager.EMPTY_STRING;
        }
        StringBuilder abbreviation = new StringBuilder();
        abbreviation.append(Character.toLowerCase(elementName.charAt(0)));
        if (elementName.matches("^[A-Z][A-Z]*(_([A-Z])[A-Z]*)+$")) {
            char previous = elementName.charAt(0);
            for (int i = 1; i < elementName.length(); i++) {
                if (previous == '_') {
                    abbreviation.append(Character.toLowerCase(elementName.charAt(i)));
                }
                previous = elementName.charAt(i);
            }
        } else {
            for (int i = 1; i < elementName.length(); i++) {
                if (Character.isUpperCase(elementName.charAt(i)) && !Character.isUpperCase(elementName.charAt(i - 1))) {
                    abbreviation.append(Character.toLowerCase(elementName.charAt(i)));
                }
            }
        }
        return abbreviation.toString();
    }

    private Optional<VariableElement> instanceOf(String typeName, String name) {
        Optional<TypeMirror> type = type(typeName);
        VariableElement closest = null;
        int distance = Integer.MAX_VALUE;
        if (type != null) {
            for (Element element : localElements) {
                if (VariableElement.class
                        .isInstance(element)
                        && !ConstantDataManager.ANGLED_ERROR.contentEquals(element.getSimpleName())
                        && element.asType().getKind() != TypeKind.ERROR
                        && type.map(t -> types.isAssignable(element.asType(), t)).orElse(false)) {
                    if (name.isEmpty()) {
                        return Optional.of((VariableElement) element);
                    }
                    int d = ElementHeaders.getDistance(element.getSimpleName().toString()
                            .toLowerCase(), name.toLowerCase());
                    if (type.map(t -> isSameType(element.asType(), t, types)).orElse(false)) {
                        d -= 1000;
                    }
                    if (d < distance) {
                        distance = d;
                        closest = (VariableElement) element;
                    }
                }
            }
        }
        return Optional.ofNullable(closest);
    }

    private Optional<TypeMirror> type(String typeName) {
        String type = typeName.trim();
        if (type.isEmpty()) {
            return Optional.empty();
        }
        Scope scope = treeUtilities.scopeFor(caretPosition);
        Optional<TreePath> currentPath = pathFor(caretPosition);
        TypeElement enclosingClass = scope.getEnclosingClass();
        SourcePositions[] sourcePositions = new SourcePositions[1];
        StatementTree statement = treeUtilities.parseStatement("{" + type + " a;}", sourcePositions); //NOI18N
        if (statement.getKind() == Tree.Kind.BLOCK) {
            List<? extends StatementTree> statements = ((BlockTree) statement).getStatements();
            if (!statements.isEmpty()) {
                StatementTree variable = statements.get(0);
                if (variable.getKind() == Tree.Kind.VARIABLE) {
                    treeUtilities.attributeTree(statement, scope);
                    return currentPath.flatMap(cp ->
                            getTypeMirror(new TreePath(cp, ((VariableTree) variable).getType())).or(() ->
                                    Optional.empty()));
                }
            }
        }
        return Optional.ofNullable(treeUtilities.parseType(type, enclosingClass));
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

    public boolean isMethodReturnVoid(ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        return returnType.getKind() == TypeKind.VOID;
    }

    public int findInsertIndexForInvocationArgument(MethodInvocationTree methodInvocationTree) {
        return findInsertIndexForArgument(methodInvocationTree.getArguments());
    }

    public int findInsertIndexForInvocationArgument(NewClassTree newClassTree) {
        return findInsertIndexForArgument(newClassTree.getArguments());
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

    private Optional<TreePath> pathFor(int caretPosition) {
        return Optional.ofNullable(treeUtilities.pathFor(caretPosition));
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

    private Optional<Element> getElement(TreePath path) {
        return Optional.ofNullable(trees.getElement(path));
    }

    private Optional<TreePath> getPath(TreePath path, Tree tree) {
        return Optional.ofNullable(TreePath.getPath(path, tree));
    }

    private Optional<TreePath> getPath(CompilationUnitTree compilationUnitTree, Tree tree) {
        return Optional.ofNullable(TreePath.getPath(compilationUnitTree, tree));
    }

    private int findIndexOfCurrentArgumentInMethod(MethodInvocationTree methodInvocationTree) {
        requireNonNull(methodInvocationTree,
                () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "methodInvocationTree"));
        List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            ExpressionTree argument = arguments.get(i);
            if (argument.toString().equals(typedAbbreviation)) {
                return i;
            }
        }
        return -1;
    }

    public int findInsertIndexInBlock(BlockTree blockTree, Tree tree) {
        requireNonNull(blockTree, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "blockTree"));
        requireNonNull(tree, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "tree"));
        List<? extends StatementTree> statements = blockTree.getStatements();
        int i;
        for (i = 0; i < statements.size(); i++) {
            if (statements.get(i).toString().equals(tree.toString())) {
                break;
            }
        }
        if (i == statements.size()) {
            return -1;
        }
        return i;
    }

    public int findInsertIndexInBlock(BlockTree blockTree) {
        requireNonNull(blockTree, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "blockTree"));
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
                if (caretPosition < currentStartPosition) {
                    return 0;
                } else {
                    return 1;
                }
            }
            case 2: {
                StatementTree previousStatement = statements.get(0);
                long previousStartPosition = sourcePositions.getStartPosition(compilationUnit, previousStatement);
                StatementTree currentStatement = statements.get(1);
                long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                if (caretPosition < previousStartPosition) {
                    return 0;
                } else if (currentStartPosition < caretPosition) {
                    return size;
                } else {
                    return 1;
                }
            }
            default: {
                for (int i = 1; i < size; i++) {
                    StatementTree previousStatement = statements.get(i - 1);
                    long previousStartPosition = sourcePositions.getStartPosition(compilationUnit, previousStatement);
                    StatementTree currentStatement = statements.get(i);
                    long currentStartPosition = sourcePositions.getStartPosition(compilationUnit, currentStatement);
                    if (i < size - 1) {
                        if (caretPosition < previousStartPosition) {
                            return i - 1;
                        } else if (previousStartPosition < caretPosition && caretPosition < currentStartPosition) {
                            return i;
                        }
                    } else {
                        if (caretPosition < currentStartPosition) {
                            return size - 1;
                        }
                        return size;
                    }
                }
            }
        }
        return -1;
    }

    List<Element> getLocalElements() {
        return Collections.unmodifiableList(localElements);
    }

    public void setTypedAbbreviation(String abbreviation) {
        this.typedAbbreviation = abbreviation;
    }

    List<MethodCall> findStaticMethodCalls(String scopeAbbreviation, String methodAbbreviation) {
        List<TypeElement> typeElements = getImportedTypesMatchingAbbreviation(scopeAbbreviation);
        List<MethodCall> methodCalls = new ArrayList<>();
        typeElements.forEach(element -> {
            List<ExecutableElement> methods = getStaticMethodsInClass(element);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodCalls.add(new MethodCall(element, method, arguments, this));
            });
        });
        return Collections.unmodifiableList(methodCalls);
    }

    private List<TypeElement> getImportedTypesMatchingAbbreviation(String typeAbbreviation) {
        List<? extends ImportTree> imports = compilationUnit.getImports();
        List<TypeElement> result = new ArrayList<>();
        imports.stream().map(importTree -> importTree.getQualifiedIdentifier().toString()).forEachOrdered(fqn -> {
            String sn = fqn.substring(fqn.lastIndexOf('.') + 1);
            if (typeAbbreviation.equals(getElementAbbreviation(sn))) {
                Optional<TypeElement> typeElement = getTypeElement(fqn);
                typeElement.ifPresent(result::add);
            }
        });
        return Collections.unmodifiableList(result);
    }

    private Optional<TypeElement> getTypeElement(String fqn) {
        return Optional.ofNullable(elements.getTypeElement(fqn));
    }

    private List<ExecutableElement> getStaticMethodsInClass(TypeElement element) {
        validate();
        List<? extends Element> members = element.getEnclosedElements();
        List<ExecutableElement> methods = ElementFilter.methodsIn(members);
        methods = filterStaticMethods(methods);
        return Collections.unmodifiableList(methods);
    }

    List<ExecutableElement> filterStaticMethods(List<ExecutableElement> methods) {
        List<ExecutableElement> staticMethods = new ArrayList<>();
        methods.stream().filter(method -> (method.getModifiers().contains(Modifier.STATIC)))
                .forEachOrdered(method -> {
                    staticMethods.add(method);
                });
        return Collections.unmodifiableList(staticMethods);
    }

    List<MethodCall> findMethodCalls(List<Element> elements, String methodAbbreviation) {
        List<MethodCall> methodCalls = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getMethodsInClassAndSuperclassesExceptStatic(element);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodCalls.add(new MethodCall(element, method, arguments, this));
            });
        });
        return Collections.unmodifiableList(methodCalls);
    }

    private Optional<TypeMirror> getTypeInContext() {
        Optional<TreePath> currentPath = pathFor(caretPosition);
        Optional<Tree> currentTree = currentPath.map(cp -> cp.getLeaf()).or(() -> Optional.empty());
        Optional<Tree.Kind> kind = currentTree.map(ct -> ct.getKind()).or(() -> Optional.empty());
        switch (kind.map(k -> k).orElse(Tree.Kind.OTHER)) {
            case ASSIGNMENT: {
                Optional<AssignmentTree> assignmentTree =
                        currentTree.map(ct -> (AssignmentTree) ct).or(() -> Optional.empty());
                Optional<ExpressionTree> variable =
                        assignmentTree.map(AssignmentTree::getVariable).or(() -> Optional.empty());
                Optional<TreePath> path = currentPath.isPresent() && variable.isPresent()
                        ? getPath(currentPath.get(), variable.get())
                        : Optional.empty();
                return path.flatMap(this::getElement).map(Element::asType).or(() -> Optional.empty());
            }
            case BLOCK:
            case PARENTHESIZED: {
                return Optional.empty();
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
                return Optional.of(types.getPrimitiveType(TypeKind.DOUBLE));
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
                return Optional.of(types.getPrimitiveType(TypeKind.LONG));
            }
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
            case LOGICAL_COMPLEMENT: {
                return Optional.of(types.getPrimitiveType(TypeKind.BOOLEAN));
            }
            case MEMBER_SELECT: {
                Optional<ExpressionTree> expression = currentTree.map(ct -> (MemberSelectTree) ct)
                        .map(MemberSelectTree::getExpression).or(() -> Optional.empty());
                if (currentPath.isPresent() && expression.isPresent()) {
                    TreePath path = TreePath.getPath(currentPath.get(), expression.get());
                    return getTypeMirror(path);
                }
                return Optional.empty();
            }
            case METHOD_INVOCATION: {
                int insertIndex =
                        currentTree.map(ct -> findIndexOfCurrentArgumentInMethod((MethodInvocationTree) ct)).orElse(-1);
                Optional<Element> element = currentPath.flatMap(this::getElement).or(() -> Optional.empty());
                if (element.map(Element::getKind).orElse(ElementKind.OTHER) == ElementKind.METHOD) {
                    return element.map(e -> (ExecutableElement) e)
                            .map(ExecutableElement::getParameters)
                            .filter(p -> insertIndex != -1)
                            .map(p -> p.get(insertIndex))
                            .map(VariableElement::asType)
                            .or(() -> Optional.empty());
                }
                break;
            }
            default: {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private List<ExecutableElement> getMethodsInClassAndSuperclassesExceptStatic(Element element) {
        List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(element);
        methods = filterNonStaticMethods(methods);
        return Collections.unmodifiableList(methods);
    }

    private List<ExecutableElement> getAllMethodsInClassAndSuperclasses(Element element) {
        validate();
        TypeMirror typeMirror = element.asType();
        Iterable<? extends Element> members = elementUtilities.getMembers(typeMirror, (e, t) -> {
            return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
        });
        List<ExecutableElement> methods = new ArrayList<>();
        Iterator<? extends Element> iterator = members.iterator();
        while (iterator.hasNext()) {
            ExecutableElement method = (ExecutableElement) iterator.next();
            methods.add(method);
        }
        return Collections.unmodifiableList(methods);
    }

    List<ExecutableElement> filterNonStaticMethods(List<ExecutableElement> methods) {
        List<ExecutableElement> staticMethods = new ArrayList<>();
        methods.stream()
                .filter(method -> (!method.getModifiers().contains(Modifier.STATIC)))
                .forEachOrdered(method -> {
                    staticMethods.add(method);
                });
        return Collections.unmodifiableList(staticMethods);
    }

    public boolean insertMethodCall(MethodCall methodCall) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                make = copy.getTreeMaker();
                treeUtilities = copy.getTreeUtilities();
                Optional<TreePath> currentPath = pathFor(caretPosition);
                currentPath.map(cp -> TreeFactory.create(cp, methodCall, copy, this)).ifPresent(it ->
                        it.insert(null));
            });
            modificationResult.commit();
            return !modificationResult.getModifiedFileObjects().isEmpty();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    List<MethodCall> findLocalMethodCalls(String methodAbbreviation) {
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses();
        methods = getMethodsByAbbreviation(methodAbbreviation, methods);
        List<MethodCall> methodCalls = new ArrayList<>();
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            methodCalls.add(new MethodCall(null, method, arguments, this));
        });
        return Collections.unmodifiableList(methodCalls);
    }

    private List<ExecutableElement> getMethodsInCurrentAndSuperclasses() {
        JavaSource js = getJavaSourceForDocument(document);
        List<ExecutableElement> methods = new ArrayList<>();
        try {
            js.runUserActionTask(controller -> {
                moveStateToResolvedPhase(controller);
                Optional<TypeMirror> typeMirror = getTypeMirrorOfCurrentClass();
                methods.addAll(typeMirror.map(tm -> {
                    return elementUtilities.getMembers(tm, (e, t) -> {
                        return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
                    });
                }).map(ElementFilter::methodsIn).orElse(Collections.emptyList()));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(methods);
    }

    private Optional<TypeMirror> getTypeMirrorOfCurrentClass() {
        Tree tree = compilationUnit.getTypeDecls().get(0);
        if (tree.getKind() == Tree.Kind.CLASS) {
            return getPath(compilationUnit, tree).flatMap(this::getTypeMirror).or(() -> Optional.empty());
        }
        return Optional.empty();
    }

    private Optional<TypeMirror> getTypeMirror(TreePath path) {
        return Optional.ofNullable(trees.getTypeMirror(path));
    }

    private List<ExecutableElement> getMethodsByAbbreviation(String methodAbbreviation, List<ExecutableElement> methods) {
        List<ExecutableElement> result = new ArrayList<>();
        methods.forEach(method -> {
            String abbreviation = getMethodAbbreviation(method.getSimpleName().toString());
            if (abbreviation.equals(methodAbbreviation)) {
                result.add(method);
            }
        });
        return Collections.unmodifiableList(result);
    }

    private String getMethodAbbreviation(String methodName) {
        StringBuilder abbreviation = new StringBuilder().append(Character.toLowerCase(methodName.charAt(0)));
        for (int i = 1; i < methodName.length(); i++) {
            if (Character.isUpperCase(methodName.charAt(i)) && Character.isLowerCase(methodName.charAt(i - 1))) {
                abbreviation.append(Character.toLowerCase(methodName.charAt(i)));
            }
        }
        return abbreviation.toString();
    }

    private List<ExpressionTree> evaluateMethodArguments(ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        List<ExpressionTree> arguments = new ArrayList<>();
        parameters.stream()
                .map(parameter -> parameter.asType())
                .forEachOrdered(elementType -> {
                    AtomicReference<IdentifierTree> identifierTree = new AtomicReference<>();
                    Optional<VariableElement> variableElement = instanceOf(elementType.toString(), "");
                    variableElement.ifPresentOrElse(ve -> {
                        identifierTree.set(make.Identifier(ve));
                        arguments.add(identifierTree.get());
                    }, () -> {
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
                    });
                });
        return Collections.unmodifiableList(arguments);
    }

    JTextComponent getComponent() {
        return component;
    }

    Document getDocument() {
        return document;
    }

    List<LocalElement> findLocalElements(String abbreviation) {
        List<LocalElement> result = new ArrayList<>();
        localElements
                .stream()
                .filter(element -> getElementAbbreviation(element.getSimpleName().toString()).equals(abbreviation))
                .forEach(element -> result.add(new LocalElement(element)));
        return Collections.unmodifiableList(result);
    }

    public boolean insertLocalElement(LocalElement element) {
        try {
            document.insertString(caretPosition, element.toString(), null);
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    Optional<Keyword> findKeyword(String keywordAbbreviation) {
        String keyword = ConstantDataManager.ABBREVIATION_TO_KEYWORD.get(keywordAbbreviation);
        if (keyword != null) {
            return Optional.of(new Keyword(keyword));
        }
        return Optional.empty();
    }

    public boolean insertKeyword(Keyword keyword) {
        try {
            document.insertString(caretPosition, keyword.toString(), null);
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    boolean isMemberSelection() {
        Optional<TreePath> path = pathFor(caretPosition);
        return path.map(TreePath::getLeaf).map(Tree::getKind).map(k -> k == Tree.Kind.MEMBER_SELECT).orElse(false);
    }

    List<MethodCall> findChainedMethodCalls(String methodAbbreviation) {
        Optional<TypeMirror> type = getTypeInContext();
        Optional<Element> typeElement = type.map(types::asElement).or(() -> Optional.empty());
        Optional<List<ExecutableElement>> methods = typeElement.map(this::getAllMethodsInClassAndSuperclasses)
                .map(m -> getMethodsByAbbreviation(methodAbbreviation, m)).or(() -> Optional.empty());
        List<MethodCall> methodCalls = new ArrayList<>();
        if (methods.isPresent() && typeElement.isPresent()) {
            methods.get().forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method);
                methodCalls.add(new MethodCall(null, method, arguments, this));
            });
        }
        return Collections.unmodifiableList(methodCalls);
    }

    List<FieldAccess> findFieldAccesses(String scopeAbbreviation, String nameAbbreviation) {
        List<TypeElement> typeElements = getImportedTypesMatchingAbbreviation(scopeAbbreviation);
        List<FieldAccess> result = new ArrayList<>();
        typeElements.forEach(typeElement -> {
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            enclosedElements.stream().filter(element ->
                    ((element.getKind() == ElementKind.FIELD
                    && element.getModifiers().contains(Modifier.PUBLIC)
                    && element.getModifiers().contains(Modifier.STATIC)
                    && element.getModifiers().contains(Modifier.FINAL))
                    || element.getKind() == ElementKind.ENUM_CONSTANT)).forEachOrdered(element -> {
                String elementName = element.getSimpleName().toString();
                String elementAbbreviation = getElementAbbreviation(elementName);
                if (nameAbbreviation.equals(elementAbbreviation)) {
                    result.add(new FieldAccess(typeElement, element));
                }
            });
        });
        return Collections.unmodifiableList(result);
    }

    public boolean insertFieldAccess(FieldAccess fieldAccess) {
        MemberSelectTree cs = make.MemberSelect(make.Identifier(fieldAccess.getScope()),
                fieldAccess.getName());
        try {
            document.insertString(caretPosition, cs.toString(), null);
            addImport(fieldAccess.getScope());
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    List<Type> findTypes(String typeAbbreviation) {
        List<TypeElement> importedTypes = getImportedTypesMatchingAbbreviation(typeAbbreviation);
        List<Type> result = new ArrayList<>();
        importedTypes.forEach(t -> result.add(new Type(t)));
        return Collections.unmodifiableList(result);
    }

    public boolean insertType(Type type) {
        try {
            document.insertString(caretPosition, type.toString(), null);
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    public void addImport(TypeElement type) {
        List<? extends ImportTree> imports = compilationUnit.getImports();
        for (ImportTree importTree : imports) {
            if (importTree.getQualifiedIdentifier().toString().equals(type.getQualifiedName().toString())) {
                return;
            }
        }
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                compilationUnit = copy.getCompilationUnit();
                make = copy.getTreeMaker();
                copy.rewrite(compilationUnit, make.addCompUnitImport(compilationUnit, make.Import(make.Identifier(type.getQualifiedName().toString()), false)));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
