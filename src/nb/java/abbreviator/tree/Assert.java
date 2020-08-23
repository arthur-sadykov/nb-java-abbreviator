/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.AssertTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.Utilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Assert extends InsertableStatementTree {

    private final AssertTree current;

    public Assert(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (AssertTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        String expression = current.getCondition().toString();
        String detail = current.getDetail().toString();
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        expression = Utilities.createExpression(expression, methodCall);
        detail = Utilities.createExpression(detail, methodCall);
        AssertTree assertTree;
        if (tree != null) {
            if (!expression.isEmpty()) {
                assertTree = make.Assert(make.Identifier(expression), current.getDetail());
            } else {
                if (detail.isEmpty()) {
                    return;
                }
                assertTree = make.Assert(current.getCondition(), make.Identifier(detail));
            }
        } else {
            if (!expression.isEmpty()) {
                assertTree = make.Assert(methodCall, current.getDetail());
            } else {
                if (detail.isEmpty()) {
                    return;
                }
                assertTree = make.Assert(current.getCondition(), methodCall);
            }
        }
        parent.insert(assertTree);
    }
}
