/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.TreeFactory;
import nb.java.abbreviator.constants.ConstantDataManager;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class InsertableTree {

    protected static final Set<Tree.Kind> KINDS = EnumSet.of(
            Tree.Kind.IF,
            Tree.Kind.WHILE_LOOP,
            Tree.Kind.RETURN,
            Tree.Kind.BLOCK,
            Tree.Kind.VARIABLE,
            Tree.Kind.ASSIGNMENT,
            Tree.Kind.METHOD_INVOCATION);
    protected final TreePath currentPath;
    protected final MethodSelectionWrapper wrapper;
    protected final TreeMaker make;
    protected final WorkingCopy copy;
    protected final JavaSourceHelper helper;
    protected final TreeUtilities treeUtilities;
    protected InsertableTree parent;

    protected InsertableTree(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) {
        requireNonNull(currentPath, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "currentPath"));
        requireNonNull(wrapper, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "wrapper"));
        requireNonNull(copy, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "copy"));
        requireNonNull(helper, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "helper"));
        this.currentPath = currentPath;
        this.wrapper = wrapper;
        this.copy = copy;
        this.helper = helper;
        make = copy.getTreeMaker();
        treeUtilities = copy.getTreeUtilities();
        TreePath parentPath = currentPath.getParentPath();
        if (parentPath != null) {
            parent = TreeFactory.create(parentPath, wrapper, copy, helper);
        }
    }

    public abstract void insert(Tree tree);
}
