/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class While extends InsertableStatementTree {

    private final WhileLoopTree current;

    public While(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (WhileLoopTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        WhileLoopTree whileLoopTree =
                make.WhileLoop(
                        (ExpressionTree) tree,
                        current.getStatement());
        parent.insert(whileLoopTree);
    }
}
