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
package com.github.isarthur.nb.java.abbreviator;

import com.github.isarthur.nb.java.abbreviator.constants.ConstantDataManager;
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

    private final Document document;
    private final List<Element> localElements;
    private Types types;
    private int caretPosition;
    private TreeMaker make;
    private TreeUtilities treeUtilities;
    private Trees trees;
    private CompilationUnitTree compilationUnit;
    private ElementUtilities elementUtilities;
    private Elements elements;
    private String typedAbbreviation;

    public JavaSourceHelper(Document document) {
        requireNonNull(document, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "document"));
        this.document = document;
        this.localElements = new ArrayList<>();
        this.caretPosition = -1;
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
            javaSource.runModificationTask(workingCopy -> {
                workingCopy.toPhase(Phase.RESOLVED);
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
                                    || e.getKind() == ElementKind.RESOURCE_VARIABLE)
                                    && !elements.isDeprecated(e));
                        });
                localMembersAndVars.forEach(localElements::add);
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ExpressionStatementTree createVoidMethodSelection(MethodSelectionWrapper wrapper) {
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                make.Identifier(wrapper.getMethod()), wrapper.getArguments());
        if (wrapper.getElement() == null) {
            return make.ExpressionStatement(methodInvocationTree);
        }
        return make.ExpressionStatement(
                make.MemberSelect(make.Identifier(wrapper.getElement()), methodInvocationTree.toString()));
    }

    public VariableTree createMethodSelectionWithReturnValue(MethodSelectionWrapper wrapper, WorkingCopy workingCopy) {
        ModifiersTree modifiers = make.Modifiers(Collections.emptySet());
        Tree type = make.Type(wrapper.getMethod().getReturnType());
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(
                Collections.emptyList(),
                make.Identifier(wrapper.getMethod()),
                wrapper.getArguments());
        ExpressionTree initializer;
        if (wrapper.getElement() == null) {
            initializer = methodInvocationTree;
        } else {
            if (isTypeElement(wrapper.getElement())) {
                initializer = make.MemberSelect(make.QualIdent(wrapper.getElement()), methodInvocationTree.toString());
            } else {
                initializer = make.MemberSelect(make.Identifier(wrapper.getElement()), methodInvocationTree.toString());
            }
        }
        Iterator<String> names = Utilities.varNamesSuggestions(wrapper.getMethod().getReturnType(),
                ElementKind.LOCAL_VARIABLE, Collections.emptySet(), null, null, workingCopy.getTypes(),
                workingCopy.getElements(), localElements, CodeStyle.getDefault(document)).iterator();
        String variableName = names.next();
        VariableTree variableTree = make.Variable(modifiers, variableName, type, initializer);
        return variableTree;
    }

    private boolean isTypeElement(Element element) {
        return TypeElement.class.isInstance(element);
    }

    public ExpressionTree createMethodSelectionWithoutReturnValue(MethodSelectionWrapper wrapper) {
        MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                make.Identifier(wrapper.getMethod()), wrapper.getArguments());
        if (wrapper.getElement() == null) {
            return methodInvocationTree;
        }
        return make.MemberSelect(make.Identifier(wrapper.getElement()), methodInvocationTree.toString());
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

    boolean insertStaticMethodSelection(String typeAbbreviation, String methodAbbreviation) {
        List<TypeElement> typeElements = getImportedTypesMatchingAbbreviation(typeAbbreviation);
        if (typeElements.isEmpty()) {
            typeElements = getTypeElementsByAbbreviationInSourceCompileAndBootPath(typeAbbreviation);
        }
        return insertMethodSelection(findStaticMethodWrapper(typeElements, methodAbbreviation));
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

    private Optional<MethodSelectionWrapper> findStaticMethodWrapper(List<TypeElement> elements,
            String methodAbbreviation) {
        Optional<MethodSelectionWrapper> resultWrapper = null;
        Optional<TypeMirror> typeInContext = getTypeInContext();
        List<Optional<MethodSelectionWrapper>> wrappers = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getStaticMethodsInClass(element);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            wrappers.add(findMethodWithLargestNumberOfResolvedArguments(element, methods, true));
        });
        int maxNumberOfResolvedArguments = Integer.MIN_VALUE;
        for (Optional<MethodSelectionWrapper> wrapper : wrappers) {
            Optional<ExecutableElement> method = wrapper.map(MethodSelectionWrapper::getMethod).or(() ->
                    Optional.empty());
            int numberOfResolvedArguments = wrapper.map(MethodSelectionWrapper::getMethod)
                    .map(ExecutableElement::getParameters).map(List::size).orElse(-1);
            if (numberOfResolvedArguments > maxNumberOfResolvedArguments) {
                Optional<TypeMirror> returnType = method.map(m -> m.getReturnType()).or(() -> Optional.empty());
                if (!typeInContext.isPresent() || typeInContext.map(tic ->
                        returnType.map(rt -> isTypesAssignable(rt, tic)).orElse(false)).orElse(false)) {
                    maxNumberOfResolvedArguments = numberOfResolvedArguments;
                    resultWrapper = wrapper;
                }
            }
        }
        return resultWrapper != null ? resultWrapper : Optional.empty();
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

    boolean insertMethodSelection(List<Element> elements, String methodAbbreviation) {
        return insertMethodSelection(findMethodWrapper(elements, methodAbbreviation));
    }

    private Optional<MethodSelectionWrapper> findMethodWrapper(List<Element> elements, String methodAbbreviation) {
        Optional<MethodSelectionWrapper> resultWrapper = null;
        Optional<TypeMirror> typeInContext = getTypeInContext();
        List<Optional<MethodSelectionWrapper>> wrappers = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getMethodsInClassAndSuperclassesExceptStatic(element);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            wrappers.add(findMethodWithLargestNumberOfResolvedArguments(element, methods, false));
        });
        int maxNumberOfResolvedArguments = Integer.MIN_VALUE;
        for (Optional<MethodSelectionWrapper> wrapper : wrappers) {
            Optional<ExecutableElement> method = wrapper.map(MethodSelectionWrapper::getMethod).or(() ->
                    Optional.empty());
            int numberOfResolvedArguments = wrapper.map(MethodSelectionWrapper::getMethod)
                    .map(ExecutableElement::getParameters).map(List::size).orElse(-1);
            if (numberOfResolvedArguments > maxNumberOfResolvedArguments) {
                Optional<TypeMirror> returnType = method.map(m -> m.getReturnType()).or(() -> Optional.empty());
                if (!typeInContext.isPresent() || typeInContext.map(tic ->
                        returnType.map(rt -> isTypesAssignable(rt, tic)).orElse(false)).orElse(false)) {
                    maxNumberOfResolvedArguments = numberOfResolvedArguments;
                    resultWrapper = wrapper;
                }
            }
        }
        return resultWrapper != null ? resultWrapper : Optional.empty();
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

    private boolean isTypesAssignable(TypeMirror t1, TypeMirror t2) {
        return types.isAssignable(t1, t2);
    }

    private boolean insertMethodSelection(Optional<MethodSelectionWrapper> wrapper) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                make = copy.getTreeMaker();
                treeUtilities = copy.getTreeUtilities();
                Optional<TreePath> currentPath = pathFor(caretPosition);
                wrapper.ifPresent(w -> {
                    currentPath.map(cp -> TreeFactory.create(cp, w, copy, this)).ifPresent(it -> it.insert(null));
                });
            });
            modificationResult.commit();
            return !modificationResult.getModifiedFileObjects().isEmpty();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    boolean insertSelectionForMethodInCurrentOrSuperclass(String methodAbbreviation) {
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses();
        methods = getMethodsByAbbreviation(methodAbbreviation, methods);
        Optional<MethodSelectionWrapper> wrapper = findMethodWithLargestNumberOfResolvedArguments(null, methods, false);
        return insertMethodSelection(wrapper);
    }

    private List<ExecutableElement> getMethodsInCurrentAndSuperclasses() {
        Optional<TypeMirror> typeMirror = getTypeMirrorOfCurrentClass();
        return typeMirror.map(tm -> {
            return elementUtilities.getMembers(tm, (e, t) -> {
                return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
            });
        }).map(ElementFilter::methodsIn).orElse(Collections.emptyList());
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

    private Optional<MethodSelectionWrapper> findMethodWithLargestNumberOfResolvedArguments(Element element,
            List<ExecutableElement> methods, boolean staticMember) {
        List<MethodSelectionWrapper> wrappers = new ArrayList<>();
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            wrappers.add(new MethodSelectionWrapper(element, method, arguments, staticMember));
        });
        wrappers.forEach(wrapper -> {
            List<ExpressionTree> arguments = wrapper.getArguments();
            int numberOfResolvedArguments = 0;
            numberOfResolvedArguments =
                    arguments.stream().filter(expressionTree -> (isArgumentResolved(expressionTree.toString())))
                            .map(item -> 1)
                            .reduce(numberOfResolvedArguments, Integer::sum);
            wrapper.setResolvedArgumentsNumber(numberOfResolvedArguments);
        });
        Collections.sort(wrappers, (o1, o2) -> {
            return Integer.compare(o1.getArgumentsNumber(), o2.getArgumentsNumber());
        });
        for (int i = wrappers.size() - 1; i >= 0; i--) {
            MethodSelectionWrapper wrapper = wrappers.get(i);
            if (wrapper.getArgumentsNumber() == wrapper.getResolvedArgumentsNumber()) {
                return Optional.ofNullable(wrapper);
            }
        }
        Collections.sort(wrappers, (o1, o2) -> {
            return Double.compare(o1.getRelation(), o2.getRelation());
        });
        if (wrappers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(wrappers.get(wrappers.size() - 1));
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

    private boolean isArgumentResolved(String argument) {
        return !argument.equals(ConstantDataManager.NULL)
                && !argument.equals(ConstantDataManager.INTEGER_ZERO_LITERAL)
                && !argument.equals(ConstantDataManager.LONG_ZERO_LITERAL)
                && !argument.equals(ConstantDataManager.DOUBLE_ZERO_LITERAL)
                && !argument.equals(ConstantDataManager.FLOAT_ZERO_LITERAL)
                && !argument.equals(ConstantDataManager.FALSE);
    }

    Document getDocument() {
        return document;
    }

    boolean insertLocalElement(List<Element> elements) {
        if (!elements.isEmpty()) {
            try {
                document.insertString(caretPosition, elements.get(0).getSimpleName().toString(), null);
                return true;
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
                return false;
            }
        }
        return false;
    }

    boolean insertKeyword(String keywordAbbreviation) {
        String keyword = ConstantDataManager.ABBREVIATION_TO_KEYWORD.get(keywordAbbreviation);
        if (keyword == null) {
            return false;
        }
        try {
            document.insertString(caretPosition, keyword, null);
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    boolean insertLocalMethod(String methodAbbreviation) {
        return insertSelectionForMethodInCurrentOrSuperclass(methodAbbreviation);
    }

    boolean isMemberSelection() {
        Optional<TreePath> path = pathFor(caretPosition);
        return path.map(TreePath::getLeaf).map(Tree::getKind).map(k -> k == Tree.Kind.MEMBER_SELECT).orElse(false);
    }

    boolean insertChainedMethodSelection(String methodAbbreviation) {
        Optional<TypeMirror> type = getTypeInContext();
        Optional<Element> typeElement = type.map(types::asElement).or(() -> Optional.empty());
        Optional<List<ExecutableElement>> methods = typeElement.map(this::getAllMethodsInClassAndSuperclasses)
                .map(m -> getMethodsByAbbreviation(methodAbbreviation, m)).or(() -> Optional.empty());
        if (methods.isPresent() && typeElement.isPresent()) {
            Optional<MethodSelectionWrapper> method =
                    findMethodWithLargestNumberOfResolvedArguments(typeElement.get(), methods.get(), false);
            method.ifPresent(m -> m.setElement(null));
            return insertMethodSelection(method);
        }
        return false;
    }

    boolean insertConstantSelection(String expressionAbbreviation, String constantAbbreviation) {
        List<TypeElement> typeElements = getImportedTypesMatchingAbbreviation(expressionAbbreviation);
        if (typeElements.isEmpty()) {
            typeElements = getTypeElementsByAbbreviationInSourceCompileAndBootPath(expressionAbbreviation);
        }
        for (TypeElement typeElement : typeElements) {
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            for (Element element : enclosedElements) {
                if (element.getKind() == ElementKind.FIELD
                        && element.getModifiers().contains(Modifier.PUBLIC)
                        && element.getModifiers().contains(Modifier.STATIC)
                        && element.getModifiers().contains(Modifier.FINAL)) {
                    String elementName = element.getSimpleName().toString();
                    String elementAbbreviation = getElementAbbreviation(elementName);
                    if (constantAbbreviation.equals(elementAbbreviation)) {
                        return insertConstantSelection(typeElement, element);
                    }
                }
            }
        }
        return false;
    }

    private boolean insertConstantSelection(TypeElement typeElement, Element constantElement) {
        MemberSelectTree constantSelection = make.MemberSelect(make.Identifier(typeElement), constantElement);
        try {
            document.insertString(caretPosition, constantSelection.toString(), null);
            addImport(typeElement);
            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    boolean insertType(String typeAbbreviation) {
        List<TypeElement> typeElements = getImportedTypesMatchingAbbreviation(typeAbbreviation);
        if (typeElements.isEmpty()) {
            typeElements = getTypeElementsByAbbreviationInSourcePath(typeAbbreviation);
        }
        if (!typeElements.isEmpty()) {
            if (insertTypeInDocument(typeElements.get(0))) {
                addImport(typeElements.get(0));
                return true;
            }
        } else {
            typeElements = getTypeElementsByAbbreviationInSourceCompileAndBootPath(typeAbbreviation);
            if (!typeElements.isEmpty()) {
                if (insertTypeInDocument(typeElements.get(0))) {
                    addImport(typeElements.get(0));
                    return true;
                }
            }
        }
        return false;
    }

    private List<TypeElement> getTypeElementsByAbbreviationInSourcePath(String abbreviation) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        ClasspathInfo classpathInfo = javaSource.getClasspathInfo();
        ClassIndex classIndex = classpathInfo.getClassIndex();
        Set<ElementHandle<TypeElement>> declaredTypes = classIndex.getDeclaredTypes(
                abbreviation.toUpperCase(),
                ClassIndex.NameKind.CAMEL_CASE,
                EnumSet.of(ClassIndex.SearchScope.SOURCE));
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

    private boolean insertTypeInDocument(TypeElement type) {
        try {
            document.insertString(caretPosition, type.getSimpleName().toString(), null);
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

    boolean isAnnotationSelection() {
        Optional<TreePath> path = pathFor(caretPosition);
        return path.map(TreePath::getLeaf).map(Tree::getKind).map(k -> k == Tree.Kind.ANNOTATION).orElse(false);
    }

    boolean insertAnnotation(String annotationAbbreviation) {
        List<TypeElement> typeElements =
                getTypeElementsByAbbreviationInSourceCompileAndBootPath(annotationAbbreviation);
        typeElements = filterAnnotations(typeElements);
        if (!typeElements.isEmpty()) {
            if (insertTypeInDocument(typeElements.get(0))) {
                addImport(typeElements.get(0));
                return true;
            }
        }
        return false;
    }

    private List<TypeElement> filterAnnotations(List<TypeElement> typeElements) {
        List<TypeElement> annotations = new ArrayList<>();
        typeElements.stream()
                .filter(typeElement ->
                        (typeElement.getKind() == ElementKind.ANNOTATION_TYPE && !elements.isDeprecated(typeElement)))
                .forEachOrdered(typeElement -> {
                    annotations.add(typeElement);
                });
        return Collections.unmodifiableList(annotations);
    }
}
