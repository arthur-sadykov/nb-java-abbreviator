/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import nb.java.abbreviator.constants.ConstantDataManager;
import nb.java.abbreviator.exception.NotFoundException;
import nb.java.abbreviator.tree.InsertableTree;
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
import org.openide.awt.NotificationDisplayer;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceHelper {

    private static final Logger LOG = Logger.getLogger(JavaSourceHelper.class.getName());
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

    void collectLocalElements(int caretPosition) {
        if (caretPosition < 0 || caretPosition >= document.getLength()) {
            throw new IllegalArgumentException(ConstantDataManager.INVALID_CARET_POSITION);
        }
        localElements.clear();
        this.caretPosition = caretPosition;
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
                Scope scope = treeUtilities.scopeFor(caretPosition);
                Iterable<? extends Element> localMembersAndVars =
                        elementUtilities.getLocalMembersAndVars(scope, (e, type) -> {
                            return (!e.getSimpleName().toString().equals(ConstantDataManager.THIS)
                                    && !e.getSimpleName().toString().equals(ConstantDataManager.SUPER)
                                    && e.getKind() != ElementKind.METHOD)
                                    && !elements.isDeprecated(e);
                        });
                localMembersAndVars.forEach(localElements::add);
            }).commit();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
        if (names.hasNext()) {
            String variableName = names.next();
            VariableTree variableTree = make.Variable(modifiers, variableName, type, initializer);
            return variableTree;
        }
        return null;
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

    List<TypeElement> getTypeElementsByAbbreviation(String abbreviation) {
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
            LOG.log(Level.SEVERE, null, ex);
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

    private VariableElement instanceOf(String typeName, String name) {
        try {
            TypeMirror type = type(typeName);
            VariableElement closest = null;
            int distance = Integer.MAX_VALUE;
            if (type != null) {
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
            }
            return closest;
        } catch (NotFoundException e) {
        }
        return null;
    }

    private TypeMirror type(String typeName) throws NotFoundException {
        String type = typeName.trim();
        if (type.isEmpty()) {
            throw new NotFoundException("Could not find out type due to typeName parameter is empty.");
        }
        try {
            Scope scope = treeUtilities.scopeFor(caretPosition);
            TreePath currentPath = pathFor(caretPosition);
            TypeElement enclosingClass = scope.getEnclosingClass();
            SourcePositions[] sourcePositions = new SourcePositions[1];
            StatementTree statement = treeUtilities.parseStatement("{" + type + " a;}", sourcePositions); //NOI18N
            if (statement.getKind() == Tree.Kind.BLOCK) {
                List<? extends StatementTree> statements = ((BlockTree) statement).getStatements();
                if (!statements.isEmpty()) {
                    StatementTree variable = statements.get(0);
                    if (variable.getKind() == Tree.Kind.VARIABLE) {
                        treeUtilities.attributeTree(statement, scope);
                        TypeMirror result = getTypeMirror(new TreePath(currentPath,
                                ((VariableTree) variable).getType()));
                        return result;
                    }
                }
                return treeUtilities.parseType(type, enclosingClass);
            }
        } catch (NotFoundException e) {
        }
        throw new NotFoundException("Could not find type at current caret position.");
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

    private TreePath pathFor(int caretPosition) throws NotFoundException {
        TreePath currentPath = treeUtilities.pathFor(caretPosition);
        if (currentPath == null) {
            throw new NotFoundException("Could not get tree path to current caret position.");
        }
        return currentPath;
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
            throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_PHASE_RESOLVED);
        }
    }

    private Element getElement(TreePath path) throws NotFoundException {
        Element element = trees.getElement(path);
        if (element == null) {
            throw new NotFoundException("The element is not available for the given tree path.");
        }
        return element;
    }

    private TreePath getPath(TreePath path, Tree tree) throws NotFoundException {
        TreePath treePath = TreePath.getPath(path, tree);
        if (treePath == null) {
            throw new NotFoundException("The tree node is not found.");
        }
        return treePath;
    }

    private TreePath getPath(CompilationUnitTree compilationUnitTree, Tree tree) throws NotFoundException {
        TreePath treePath = TreePath.getPath(compilationUnitTree, tree);
        if (treePath == null) {
            throw new NotFoundException("The tree node is not found.");
        }
        return treePath;
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

    public int getInsertIndexInBlock(BlockTree blockTree, Tree tree) {
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

    public int getInsertIndexInBlock(BlockTree blockTree) {
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

    boolean insertStaticMethodSelection(List<TypeElement> elements, String methodAbbreviation) throws NotFoundException {
        return insertMethodSelection(getStaticMethodWrapper(elements, methodAbbreviation));
    }

    private MethodSelectionWrapper getStaticMethodWrapper(List<TypeElement> elements, String methodAbbreviation)
            throws NotFoundException {
        MethodSelectionWrapper resultWrapper = null;
        TypeMirror typeInContext = getTypeInContext();
        List<MethodSelectionWrapper> wrappers = new ArrayList<>();
        elements.forEach(element -> {
            try {
                List<ExecutableElement> methods = getStaticMethodsInClass(element);
                methods = getMethodsByAbbreviation(methodAbbreviation, methods);
                wrappers.add(findMethodWithLargestNumberOfResolvedArguments(element, methods));
            } catch (NotFoundException ex) {
            }
        });
        int maxNumberOfResolvedArguments = Integer.MIN_VALUE;
        for (MethodSelectionWrapper wrapper : wrappers) {
            ExecutableElement method = wrapper.getMethod();
            int numberOfResolvedArguments = method.getParameters().size();
            if (numberOfResolvedArguments > maxNumberOfResolvedArguments) {
                if (typeInContext == null || isTypesAssignable(method.getReturnType(), typeInContext)) {
                    maxNumberOfResolvedArguments = numberOfResolvedArguments;
                    resultWrapper = wrapper;
                }
            }
        }
        if (resultWrapper == null) {
            throw new NotFoundException("Could not find element or method.");
        }
        return resultWrapper;
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

    boolean insertCallToMethod(List<Element> elements, String methodAbbreviation) throws NotFoundException {
        return insertMethodSelection(findMethodWrapper(elements, methodAbbreviation));
    }

    private MethodSelectionWrapper findMethodWrapper(List<Element> elements, String methodAbbreviation)
            throws NotFoundException {
        MethodSelectionWrapper resultWrapper = null;
        TypeMirror typeInContext = getTypeInContext();
        List<MethodSelectionWrapper> wrappers = new ArrayList<>();
        elements.forEach(element -> {
            try {
                List<ExecutableElement> methods = getMethodsInClassAndSuperclasses(element);
                methods = getMethodsByAbbreviation(methodAbbreviation, methods);
                wrappers.add(findMethodWithLargestNumberOfResolvedArguments(element, methods));
            } catch (NotFoundException ex) {
            }
        });
        int maxNumberOfResolvedArguments = Integer.MIN_VALUE;
        for (MethodSelectionWrapper wrapper : wrappers) {
            ExecutableElement method = wrapper.getMethod();
            int numberOfResolvedArguments = method.getParameters().size();
            if (numberOfResolvedArguments > maxNumberOfResolvedArguments) {
                if (typeInContext == null || isTypesAssignable(method.getReturnType(), typeInContext)) {
                    maxNumberOfResolvedArguments = numberOfResolvedArguments;
                    resultWrapper = wrapper;
                }
            }
        }
        if (resultWrapper == null) {
            throw new NotFoundException("Could not find element or method.");
        }
        return resultWrapper;
    }

    private TypeMirror getTypeInContext() throws NotFoundException {
        TreePath currentPath = pathFor(caretPosition);
        Tree currentTree = currentPath.getLeaf();
        Tree.Kind kind = currentTree.getKind();
        switch (kind) {
            case ASSIGNMENT: {
                AssignmentTree assignmentTree = (AssignmentTree) currentTree;
                ExpressionTree variable = assignmentTree.getVariable();
                TreePath path = getPath(currentPath, variable);
                Element element = getElement(path);
                return element.asType();
            }
            case BLOCK:
            case PARENTHESIZED: {
                return null;
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
            case METHOD_INVOCATION: {
                int insertIndex = findIndexOfCurrentArgumentInMethod((MethodInvocationTree) currentTree);
                Element element = getElement(currentPath);
                if (element.getKind() == ElementKind.METHOD) {
                    ExecutableElement method = (ExecutableElement) element;
                    List<? extends VariableElement> parameters = method.getParameters();
                    if (insertIndex != -1) {
                        VariableElement parameter = parameters.get(insertIndex);
                        return parameter.asType();
                    }
                }
                break;
            }
            default: {
                NotificationDisplayer.getDefault().notify(kind.toString(), new ImageIcon(), "", null);
            }
        }
        return null;
    }

    private List<ExecutableElement> getMethodsInClassAndSuperclasses(Element element) {
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
        methods = filterNonStaticMethods(methods);
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

    private boolean insertMethodSelection(MethodSelectionWrapper wrapper) {
        JavaSource javaSource = getJavaSourceForDocument(document);
        try {
            ModificationResult modificationResult = javaSource.runModificationTask(copy -> {
                moveStateToResolvedPhase(copy);
                make = copy.getTreeMaker();
                treeUtilities = copy.getTreeUtilities();
                TreePath currentPath = pathFor(caretPosition);
                InsertableTree currentTree = TreeFactory.create(currentPath, wrapper, copy, this);
                currentTree.insert(null);
            });
            modificationResult.commit();
            return !modificationResult.getModifiedFileObjects().isEmpty();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    boolean insertSelectionForMethodInCurrentOrSuperclass(String methodAbbreviation) throws NotFoundException {
        List<ExecutableElement> methods = getMethodsInCurrentAndSuperclasses();
        methods = getMethodsByAbbreviation(methodAbbreviation, methods);
        MethodSelectionWrapper wrapper = findMethodWithLargestNumberOfResolvedArguments(null, methods);
        return insertMethodSelection(wrapper);
    }

    private List<ExecutableElement> getMethodsInCurrentAndSuperclasses() throws NotFoundException {
        TypeMirror typeMirror = getTypeMirrorOfCurrentClass();
        Iterable<? extends Element> members = elementUtilities.getMembers(typeMirror, (e, t) -> {
            return e.getKind() == ElementKind.METHOD && !elements.isDeprecated(e);
        });
        return ElementFilter.methodsIn(members);
    }

    private TypeMirror getTypeMirrorOfCurrentClass() throws NotFoundException {
        Tree tree = compilationUnit.getTypeDecls().get(0);
        if (tree.getKind() == Tree.Kind.CLASS) {
            TreePath path = getPath(compilationUnit, tree);
            return getTypeMirror(path);
        }
        throw new NotFoundException("Could not get a type of current class.");
    }

    private TypeMirror getTypeMirror(TreePath path) throws NotFoundException {
        TypeMirror typeMirror = trees.getTypeMirror(path);
        if (typeMirror == null) {
            throw new NotFoundException("Could not get a type at the given tree path.");
        }
        return typeMirror;
    }

    private List<ExecutableElement> getMethodsByAbbreviation(String methodAbbreviation, List<ExecutableElement> methods) {
        List<ExecutableElement> result = new ArrayList<>();
        methods.forEach(method -> {
            String abbreviation = getMethodAbbreviation(method.getSimpleName().toString());
            if (abbreviation.equals(methodAbbreviation)) {
                result.add(method);
            }
        });
        return result;
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

    private MethodSelectionWrapper findMethodWithLargestNumberOfResolvedArguments(Element element,
            List<ExecutableElement> methods) throws NotFoundException {
        List<MethodSelectionWrapper> wrappers = new ArrayList<>();
        methods.forEach(method -> {
            List<ExpressionTree> arguments = evaluateMethodArguments(method);
            wrappers.add(new MethodSelectionWrapper(element, method, arguments));
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
                return wrapper;
            }
        }
        Collections.sort(wrappers, (o1, o2) -> {
            return Double.compare(o1.getRelation(), o2.getRelation());
        });
        if (wrappers.isEmpty()) {
            throw new NotFoundException("Could not find requested method.");
        }
        MethodSelectionWrapper wrapper = wrappers.get(wrappers.size() - 1);
        if (wrapper.getMethod() == null) {
            throw new NotFoundException("Could not find requested method.");
        }
        return wrapper;
    }

    private List<ExpressionTree> evaluateMethodArguments(ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        List<ExpressionTree> arguments = new ArrayList<>();
        parameters.stream()
                .map(parameter -> parameter.asType())
                .forEachOrdered(elementType -> {
                    IdentifierTree identifierTree;
                    VariableElement variableElement = instanceOf(elementType.toString(), "");
                    if (variableElement != null) {
                        identifierTree = make.Identifier(variableElement);
                        arguments.add(identifierTree);
                    } else {
                        switch (elementType.getKind()) {
                            case BOOLEAN:
                                identifierTree = make.Identifier("false");
                                break;
                            case BYTE:
                            case SHORT:
                            case INT:
                                identifierTree = make.Identifier("0");
                                break;
                            case LONG:
                                identifierTree = make.Identifier("0L");
                                break;
                            case FLOAT:
                                identifierTree = make.Identifier("0.0F");
                                break;
                            case DOUBLE:
                                identifierTree = make.Identifier("0.0");
                                break;
                            default:
                                identifierTree = make.Identifier("null");
                        }
                        arguments.add(identifierTree);
                    }
                });
        return arguments;
    }

    private boolean isArgumentResolved(String argument) {
        return !argument.equals("null")
                && !argument.equals("0")
                && !argument.equals("0L")
                && !argument.equals("0.0")
                && !argument.equals("0.0F")
                && !argument.equals("false");
    }
}
