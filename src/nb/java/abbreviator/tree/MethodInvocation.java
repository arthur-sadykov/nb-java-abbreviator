/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.logging.Logger;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.Utilities;
import nb.java.abbreviator.exception.NotFoundException;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodInvocation extends InsertableExpressionTree {

    private static final Logger LOG = Logger.getLogger(MethodInvocation.class.getName());
    private final MethodInvocationTree current;

    public MethodInvocation(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (MethodInvocationTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        MethodInvocationTree methodInvocationTree = current;
        int insertIndex = helper.findInsertIndexForInvocationArgument(methodInvocationTree);
        if (insertIndex == -1) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        if (tree != null) {
            String expression = null;
            if (!methodInvocationTree.getArguments().isEmpty()) {
                expression = methodInvocationTree.getArguments().get(insertIndex).toString();
                methodInvocationTree = make.removeMethodInvocationArgument(methodInvocationTree, insertIndex);
            }
            if (expression == null) {
                return;
            }
            expression = Utilities.createExpression(expression, methodCall);
            methodInvocationTree = make.insertMethodInvocationArgument(
                    methodInvocationTree,
                    insertIndex,
                    make.Identifier(expression));
        } else {
            methodInvocationTree = make.insertMethodInvocationArgument(
                    methodInvocationTree,
                    insertIndex,
                    methodCall);
        }
        copy.rewrite(current, methodInvocationTree);
    }
}
