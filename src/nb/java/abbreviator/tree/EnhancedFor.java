/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.EnhancedForLoopTree;
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
public class EnhancedFor extends InsertableStatementTree {

    private final EnhancedForLoopTree current;

    public EnhancedFor(TreePath currentPath, MethodSelectionWrapper wrapper,
            WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (EnhancedForLoopTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        EnhancedForLoopTree enhancedForLoopTree;
        if (tree != null) {
            String expression = current.getExpression().toString();
            expression = Utilities.createExpression(expression, methodCall);
            enhancedForLoopTree =
                    make.EnhancedForLoop(
                            current.getVariable(),
                            make.Identifier(expression),
                            current.getStatement());
        } else {
            enhancedForLoopTree =
                    make.EnhancedForLoop(
                            current.getVariable(),
                            methodCall,
                            current.getStatement());
        }
        parent.insert(enhancedForLoopTree);
    }
}
