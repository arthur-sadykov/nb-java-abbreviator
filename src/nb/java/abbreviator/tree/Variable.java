/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import nb.java.abbreviator.Utilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Variable extends InsertableStatementTree {

    private final VariableTree current;

    public Variable(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (VariableTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodSelectionWithoutReturnValue(wrapper);
        VariableTree variableTree;
        if (tree != null) {
            String expression = current.getInitializer().toString();
            expression = Utilities.createExpression(expression, methodCall);
            variableTree =
                    make.Variable(
                            current.getModifiers(),
                            current.getName(),
                            current.getType(),
                            make.Identifier(expression));
        } else {
            variableTree =
                    make.Variable(
                            current.getModifiers(),
                            current.getName(),
                            current.getType(),
                            methodCall);
        }
        parent.insert(variableTree);
    }
}
