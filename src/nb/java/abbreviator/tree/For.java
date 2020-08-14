/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.Utilities;
import nb.java.abbreviator.exception.NotFoundException;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class For extends InsertableStatementTree {

    private final ForLoopTree current;

    public For(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (ForLoopTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        String expression = current.getCondition().toString();
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        ForLoopTree forLoopTree;
        if (tree != null) {
            expression = Utilities.createExpression(expression, methodCall);
            forLoopTree =
                    make.ForLoop(
                            current.getInitializer(),
                            make.Identifier(expression),
                            current.getUpdate(),
                            current.getStatement());
        } else {
            forLoopTree =
                    make.ForLoop(
                            current.getInitializer(),
                            methodCall,
                            current.getUpdate(),
                            current.getStatement());
        }
        parent.insert(forLoopTree);
    }
}
