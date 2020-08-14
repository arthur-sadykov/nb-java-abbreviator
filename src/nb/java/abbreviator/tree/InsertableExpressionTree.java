/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.TreeFactory;
import nb.java.abbreviator.exception.NotFoundException;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class InsertableExpressionTree extends InsertableTree {

    private final ExpressionTree current;

    public InsertableExpressionTree(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (ExpressionTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        TreePath parentPath = treeUtilities.getPathElementOfKind(
                EnumSet.of(
                        Tree.Kind.ASSERT,
                        Tree.Kind.ASSIGNMENT,
                        Tree.Kind.EXPRESSION_STATEMENT,
                        Tree.Kind.ENHANCED_FOR_LOOP,
                        Tree.Kind.FOR_LOOP,
                        Tree.Kind.METHOD_INVOCATION,
                        Tree.Kind.PARENTHESIZED,
                        Tree.Kind.RETURN,
                        Tree.Kind.VARIABLE),
                currentPath);
        if (parentPath == null) {
            return;
        }
        parent = TreeFactory.create(parentPath, wrapper, copy, helper);
        parent.insert(current);
    }
}
