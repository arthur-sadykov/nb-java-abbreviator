/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Synchronized extends InsertableStatementTree {

    private final SynchronizedTree current;

    public Synchronized(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (SynchronizedTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        SynchronizedTree synchronizedTree =
                make.Synchronized(
                        (ExpressionTree) tree,
                        current.getBlock());
        parent.insert(synchronizedTree);
    }
}
