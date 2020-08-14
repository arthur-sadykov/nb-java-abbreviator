/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
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
public class Assignment extends InsertableExpressionTree {

    private final AssignmentTree current;

    public Assignment(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper)
            throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (AssignmentTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        AssignmentTree assignmentTree;
        if (tree != null) {
            String expression = current.getExpression().toString();
            expression = Utilities.createExpression(expression, methodCall);
            assignmentTree = make.Assignment(current.getVariable(), make.Identifier(expression));
        } else {
            assignmentTree = make.Assignment(current.getVariable(), methodCall);
        }
        parent.insert(assignmentTree);
    }
}
