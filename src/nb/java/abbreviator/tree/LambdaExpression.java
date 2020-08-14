/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LambdaExpressionTree;
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
public class LambdaExpression extends InsertableExpressionTree {

    private final LambdaExpressionTree current;

    public LambdaExpression(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (LambdaExpressionTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        ExpressionTree lambdaExpressionTree = make.Parenthesized(methodCall);
        parent.insert(tree);
    }
}
