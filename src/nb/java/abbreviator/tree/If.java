/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.exception.NotFoundException;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class If extends InsertableStatementTree {

    private final IfTree current;

    public If(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (IfTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        IfTree ifTree = null;
        if (tree.getKind() == Tree.Kind.IF) {
            ifTree =
                    make.If(
                            current.getCondition(),
                            current.getThenStatement(),
                            (StatementTree) tree);
        } else {
            if (ExpressionTree.class.isInstance(tree)) {
                ifTree =
                        make.If(
                                (ExpressionTree) tree,
                                current.getThenStatement(),
                                current.getElseStatement());
            }
        }
        parent.insert(ifTree);
    }
}
