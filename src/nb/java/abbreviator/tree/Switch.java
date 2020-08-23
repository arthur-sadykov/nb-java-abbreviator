/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Switch extends InsertableStatementTree {

    private final SwitchTree current;

    public Switch(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (SwitchTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        SwitchTree switchTree =
                make.Switch(
                        (ExpressionTree) tree,
                        current.getCases());
        parent.insert(switchTree);
    }
}
