/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
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
public class NewClass extends InsertableExpressionTree {

    private final NewClassTree current;

    public NewClass(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (NewClassTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        NewClassTree newClassTree = current;
        int insertIndex = helper.findInsertIndexForInvocationArgument(newClassTree);
        if (insertIndex == -1) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        if (tree != null) {
            String expression = null;
            if (!newClassTree.getArguments().isEmpty()) {
                expression = newClassTree.getArguments().get(insertIndex).toString();
                newClassTree = make.removeNewClassArgument(newClassTree, insertIndex);
            }
            if (expression == null) {
                return;
            }
            expression = Utilities.createExpression(expression, methodCall);
            newClassTree = make.insertNewClassArgument(
                    newClassTree,
                    insertIndex,
                    make.Identifier(expression));
        } else {
            newClassTree = make.insertNewClassArgument(
                    newClassTree,
                    insertIndex,
                    methodCall);
        }
        copy.rewrite(current, newClassTree);
    }
}
