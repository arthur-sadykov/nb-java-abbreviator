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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.MethodCall;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Name;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
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

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceHelper {

    private final JTextComponent component;
    private final Document document;
    private String typedAbbreviation;

    public JavaSourceHelper(JTextComponent component) {
        requireNonNull(component, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "component"));
        this.component = component;
        this.document = component.getDocument();
    }

    private Set<ElementKind> getRequiredLocalElementKinds() {
        Set<ElementKind> result = new HashSet<>(Byte.SIZE);
        if (Settings.getSettingForLocalVariable()) {
            result.add(ElementKind.LOCAL_VARIABLE);
        }
        if (Settings.getSettingForField()) {
            result.add(ElementKind.FIELD);
        }
        if (Settings.getSettingForParameter()) {
            result.add(ElementKind.PARAMETER);
        }
        if (Settings.getSettingForEnumConstant()) {
            result.add(ElementKind.ENUM_CONSTANT);
        }
        if (Settings.getSettingForExceptionParameter()) {
            result.add(ElementKind.EXCEPTION_PARAMETER);
        }
        if (Settings.getSettingForResourceVariable()) {
            result.add(ElementKind.RESOURCE_VARIABLE);
        }
        if (Settings.getSettingForInternalType()) {
            result.add(ElementKind.CLASS);
            result.add(ElementKind.INTERFACE);
            result.add(ElementKind.ENUM);
        }
        return Collections.unmodifiableSet(result);
    }

    public ExpressionStatementTree createVoidMethodCall(MethodCall methodCall) {
        AtomicReference<ExpressionStatementTree> expressionStatement = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodCall.getMethod()), methodCall.getArguments());
                if (methodCall.getScope() == null) {
                    expressionStatement.set(make.ExpressionStatement(methodInvocationTree));
                } else {
                    expressionStatement.set(make.ExpressionStatement(make.MemberSelect(
                            make.Identifier(methodCall.getScope()), methodInvocationTree.toString())));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return expressionStatement.get();
    }

    public VariableTree createMethodCallWithReturnValue(MethodCall methodCall, int position) {
        AtomicReference<VariableTree> variable = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeMaker make = copy.getTreeMaker();
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
                        initializer =
                                make.MemberSelect(make.QualIdent(methodCall.getScope()), methodInvocationTree.toString());
                    } else {
                        initializer =
                                make.MemberSelect(make.Identifier(methodCall.getScope()), methodInvocationTree.toString());
                    }
                }
                Set<String> variableNames = getVariableNames(methodCall.getMethod().getReturnType(), position);
                String variableName = variableNames.isEmpty() ? "" : variableNames.iterator().next(); //NOI18N
                variable.set(make.Variable(modifiers, variableName, type, initializer));
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return variable.get();
    }

    private Set<String> getVariableNames(TypeMirror typeMirror, int position) {
        Set<String> names = new HashSet<>();
        try {
            List<Element> localElements = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(position);
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

    List<Name> findVariableNames(String abbreviation, int position) {
        List<Name> names = new ArrayList<>();
        TypeMirror type = getTypeInContext(position);
        Set<String> variableNames = getVariableNames(type, position);
        variableNames.stream()
                .filter(name -> getElementAbbreviation(name).equals(abbreviation))
                .forEach(name -> names.add(new Name(name)));
        return Collections.unmodifiableList(names);
    }

    private boolean isTypeElement(Element element) {
        return TypeElement.class.isInstance(element);
    }

    public ExpressionTree createMethodCallWithoutReturnValue(MethodCall methodCall) {
        AtomicReference<ExpressionTree> expression = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeMaker make = copy.getTreeMaker();
                MethodInvocationTree methodInvocationTree = make.MethodInvocation(Collections.emptyList(),
                        make.Identifier(methodCall.getMethod()), methodCall.getArguments());
                if (methodCall.getScope() == null) {
                    expression.set(methodInvocationTree);
                } else {
                    expression.set(make.MemberSelect(make.Identifier(
                            methodCall.getScope()), methodInvocationTree.toString()));
                }
            }).commit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return expression.get();
    }

    List<Element> getElementsByAbbreviation(String abbreviation, int position) throws IllegalArgumentException {
        List<Element> localElements = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(position);
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
            String elementAbbreviation = getElementAbbreviation(elementName);
            return !elementAbbreviation.equals(abbreviation);
        });
        return Collections.unmodifiableList(localElements);
    }

    List<TypeElement> findTypeElementsByAbbreviationInSourcePath(String abbreviation) {
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
                Elements elements = compilationController.getElements();
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

    private VariableElement instanceOf(String typeName, String name, int position) {
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
                Scope scope = treeUtilities.scopeFor(position);
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!elements.isDeprecated(e))
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && getRequiredLocalElementKinds().contains(e.getKind());
                        });
                localMembersAndVars.forEach(localElements::add);
                TypeMirror type = type(typeName, position);
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

    private TypeMirror type(String typeName, int position) {
        AtomicReference<TypeMirror> typeMirror = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Trees trees = copy.getTrees();
                Scope scope = treeUtilities.scopeFor(position);
                String type = typeName.trim();
                if (type.isEmpty()) {
                    return;
                }
                TreePath currentPath = treeUtilities.pathFor(position);
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

    public int findInsertIndexInBlock(BlockTree blockTree, int position) {
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
                        if (position < currentStartPosition) {
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
                        if (position < previousStartPosition) {
                            insertIndex.set(0);
                            break;
                        } else if (currentStartPosition < position) {
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
                                if (position < previousStartPosition) {
                                    insertIndex.set(i - 1);
                                    break;
                                } else if (previousStartPosition < position && position < currentStartPosition) {
                                    insertIndex.set(i);
                                    break;
                                }
                            } else {
                                if (position < currentStartPosition) {
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

    public void setTypedAbbreviation(String abbreviation) {
        this.typedAbbreviation = abbreviation;
    }

    List<MethodCall> findStaticMethodCalls(String scopeAbbreviation, String methodAbbreviation, int position) {
        List<TypeElement> typeElements = findTypeElementsByAbbreviationInSourcePath(scopeAbbreviation);
        List<MethodCall> methodCalls = new ArrayList<>();
        typeElements.forEach(element -> {
            List<ExecutableElement> methods = getStaticMethodsInClass(element);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method, position);
                methodCalls.add(new MethodCall(element, method, arguments, this, position));
            });
        });
        return Collections.unmodifiableList(methodCalls);
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

    List<ExecutableElement> filterStaticMethods(List<ExecutableElement> methods) {
        List<ExecutableElement> staticMethods = new ArrayList<>();
        methods.stream().filter(method -> (method.getModifiers().contains(Modifier.STATIC)))
                .forEachOrdered(method -> {
                    staticMethods.add(method);
                });
        return Collections.unmodifiableList(staticMethods);
    }

    List<MethodCall> findMethodCalls(List<Element> elements, String methodAbbreviation, int position) {
        List<MethodCall> methodCalls = new ArrayList<>();
        elements.forEach(element -> {
            List<ExecutableElement> methods = getMethodsInClassAndSuperclassesExceptStatic(element, position);
            methods = getMethodsByAbbreviation(methodAbbreviation, methods);
            methods.forEach(method -> {
                List<ExpressionTree> arguments = evaluateMethodArguments(method, position);
                methodCalls.add(new MethodCall(element, method, arguments, this, position));
            });
        });
        return Collections.unmodifiableList(methodCalls);
    }

    private TypeMirror getTypeInContext(int position) {
        AtomicReference<TypeMirror> typeMirror = new AtomicReference<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                Trees trees = copy.getTrees();
                Types types = copy.getTypes();
                TreePath currentPath = treeUtilities.pathFor(position);
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

    private List<ExecutableElement> getMethodsInClassAndSuperclassesExceptStatic(Element element, int position) {
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

    List<ExecutableElement> filterNonStaticMethods(List<ExecutableElement> methods) {
        List<ExecutableElement> staticMethods = new ArrayList<>();
        methods.stream()
                .filter(method -> (!method.getModifiers().contains(Modifier.STATIC)))
                .forEachOrdered(method -> {
                    staticMethods.add(method);
                });
        return Collections.unmodifiableList(staticMethods);
    }

    public List<CodeFragment> insertMethodCall(MethodCall methodCall, int position) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(position);
                if (currentPath == null) {
                    return;
                }
                TreeFactory.create(currentPath, methodCall, copy, this, position).insert(null);
            });
            modificationResult.commit();
            return !modificationResult.getModifiedFileObjects().isEmpty() ? Collections.singletonList(methodCall) : null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<CodeFragment> insertChainedMethodCall(MethodCall methodCall, int position) {
        try {
            document.insertString(position, methodCall.toString(), null);
            return Collections.singletonList(methodCall);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    List<MethodCall> findLocalMethodCalls(String methodAbbreviation, int position) {
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses();
        methods = getMethodsByAbbreviation(methodAbbreviation, methods);
        List<MethodCall> methodCalls = new ArrayList<>();
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method, position);
            methodCalls.add(new MethodCall(null, method, arguments, this, position));
        });
        return Collections.unmodifiableList(methodCalls);
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

    private List<ExpressionTree> evaluateMethodArguments(ExecutableElement method, int position) {
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
                            VariableElement variableElement = instanceOf(elementType.toString(), "", position);
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

    JTextComponent getComponent() {
        return component;
    }

    Document getDocument() {
        return document;
    }

    List<LocalElement> findLocalElements(String abbreviation, int position) {
        List<LocalElement> result = new ArrayList<>();
        try {
            List<Element> localElements = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(position);
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
                        .filter(element ->
                                getElementAbbreviation(element.getSimpleName().toString()).equals(abbreviation))
                        .filter(distinctByKey(Element::getSimpleName))
                        .forEach(element -> result.add(new LocalElement(element)));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertLocalElement(LocalElement element, int position) {
        try {
            document.insertString(position, element.toString(), null);
            return Collections.singletonList(element);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    List<Keyword> findKeywords(String keywordAbbreviation) {
        List<Keyword> result = new ArrayList<>();
        ConstantDataManager.KEYWORDS.forEach(keyword -> {
            String abbreviation = getElementAbbreviation(keyword);
            if (keywordAbbreviation.equals(abbreviation)) {
                result.add(new Keyword(keyword));
            }
        });
        Collections.sort(result, (keyword1, keyword2) -> {
            return keyword1.toString().compareTo(keyword2.toString());
        });
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertKeyword(Keyword keyword, int position) {
        try {
            document.insertString(position, keyword.toString(), null);
            return Collections.singletonList(keyword);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    boolean isMemberSelection(int position) {
        AtomicBoolean memberSelection = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(position);
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

    boolean isFieldOrParameterName(int position) {
        AtomicBoolean memberSelection = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = treeUtilities.pathFor(position);
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

    List<MethodCall> findChainedMethodCalls(String methodAbbreviation, int position) {
        List<MethodCall> methodCalls = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                Types types = copy.getTypes();
                TypeMirror type = getTypeInContext(position);
                if (type == null) {
                    return;
                }
                Element typeElement = types.asElement(type);
                if (typeElement == null) {
                    return;
                }
                List<ExecutableElement> methods = getAllMethodsInClassAndSuperclasses(typeElement);
                methods = getMethodsByAbbreviation(methodAbbreviation, methods);
                methods.forEach(method -> {
                    List<ExpressionTree> arguments = evaluateMethodArguments(method, position);
                    methodCalls.add(new MethodCall(null, method, arguments, this, position));
                });
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(methodCalls);
    }

    List<FieldAccess> findFieldAccesses(String scopeAbbreviation, String nameAbbreviation) {
        List<TypeElement> typeElements = findTypeElementsByAbbreviationInSourcePath(scopeAbbreviation);
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
                    String elementAbbreviation = getElementAbbreviation(elementName);
                    if (nameAbbreviation.equals(elementAbbreviation)) {
                        result.add(new FieldAccess(typeElement, element));
                    }
                });
            } catch (AssertionError ex) {
            }
        });
        return Collections.unmodifiableList(result);
    }

    public List<CodeFragment> insertFieldAccess(FieldAccess fieldAccess, int position) {
        List<CodeFragment> fieldAccesses = new ArrayList<>();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                CompilationUnitTree compilationUnit = copy.getCompilationUnit();
                TreeMaker make = copy.getTreeMaker();
                MemberSelectTree cs = make.MemberSelect(make.Identifier(fieldAccess.getScope()), fieldAccess.getName());
                try {
                    document.insertString(position, cs.toString(), null);
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

    List<Type> findTypes(String typeAbbreviation) {
        List<TypeElement> typeElements = findTypeElementsByAbbreviationInSourcePath(typeAbbreviation);
        List<Type> result = new ArrayList<>();
        typeElements = typeElements.stream()
                .filter(distinctByKey(element -> element.getSimpleName().toString()))
                .collect(Collectors.toList());
        typeElements.forEach(t -> result.add(new Type(t)));
        return Collections.unmodifiableList(result);
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<CodeFragment> insertType(Type type, int position) {
        try {
            document.insertString(position, type.toString(), null);
            return Collections.singletonList(type);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<CodeFragment> insertName(Name name, int position) {
        try {
            document.insertString(position, name.toString(), null);
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

    boolean afterThis(int position) {
        AtomicBoolean afterThis = new AtomicBoolean();
        try {
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TokenHierarchy<?> tokenHierarchy = copy.getTokenHierarchy();
                TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence();
                tokenSequence.move(position);
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

    List<LocalElement> findFields(String abbreviation, int position) {
        List<LocalElement> result = new ArrayList<>();
        try {
            List<Element> fields = new ArrayList<>();
            JavaSource javaSource = getJavaSourceForDocument(document);
            javaSource.runUserActionTask(copy -> {
                copy.toPhase(Phase.RESOLVED);
                TreeUtilities treeUtilities = copy.getTreeUtilities();
                ElementUtilities elementUtilities = copy.getElementUtilities();
                Elements elements = copy.getElements();
                Scope scope = treeUtilities.scopeFor(position);
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
                        .filter(element ->
                                getElementAbbreviation(element.getSimpleName().toString()).equals(abbreviation))
                        .filter(distinctByKey(Element::getSimpleName))
                        .forEach(element -> result.add(new LocalElement(element)));
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(result);
    }
}
