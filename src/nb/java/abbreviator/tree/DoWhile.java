/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class DoWhile extends InsertableStatementTree {

    private final DoWhileLoopTree current;

    public DoWhile(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (DoWhileLoopTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        DoWhileLoopTree doWhileLoopTree =
                make.DoWhileLoop(
                        (ExpressionTree) tree,
                        current.getStatement());
        parent.insert(doWhileLoopTree);
    }
}
