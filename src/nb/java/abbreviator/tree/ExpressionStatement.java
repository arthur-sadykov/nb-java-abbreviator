/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionStatementTree;
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
public class ExpressionStatement extends InsertableStatementTree {

    private final ExpressionStatementTree current;

    public ExpressionStatement(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (ExpressionStatementTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        ExpressionStatementTree expressionStatementTree;
        if (tree != null) {
            String expression = current.getExpression().toString();
            expression = Utilities.createExpression(expression, methodCall);
            expressionStatementTree = make.ExpressionStatement(make.Identifier(expression));
        } else {
            expressionStatementTree = make.ExpressionStatement(methodCall);
        }
        parent.insert(expressionStatementTree);
    }
}
