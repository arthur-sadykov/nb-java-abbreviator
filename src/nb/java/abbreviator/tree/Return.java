/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ReturnTree;
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
public class Return extends InsertableStatementTree {

    private final ReturnTree current;

    public Return(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) throws NotFoundException {
        super(currentPath, wrapper, copy, helper);
        current = (ReturnTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) throws NotFoundException {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        ReturnTree returnTree;
        if (tree != null) {
            String expression = current.getExpression().toString();
            expression = Utilities.createExpression(expression, methodCall);
            returnTree = make.Return(make.Identifier(expression));
        } else {
            returnTree = make.Return(methodCall);
        }
        parent.insert(returnTree);
    }
}
