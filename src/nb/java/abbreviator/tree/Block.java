/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Block extends InsertableStatementTree {

    private final BlockTree current;

    public Block(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (BlockTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        BlockTree newBlockTree = current;
        if (tree != null) {
            int insertIndex = helper.findInsertIndexInBlock(newBlockTree);
            if (insertIndex == -1) {
                return;
            }
            insertIndex--;
            newBlockTree = make.removeBlockStatement(newBlockTree, insertIndex);
            newBlockTree = make.insertBlockStatement(newBlockTree, insertIndex, (StatementTree) tree);
        } else {
            int insertIndex = helper.findInsertIndexInBlock(newBlockTree);
            if (insertIndex == -1) {
                return;
            }
            if (helper.isMethodReturnVoid(wrapper.getMethod())) {
                ExpressionStatementTree methodCall = helper.createVoidMethodSelection(wrapper);
                newBlockTree = make.insertBlockStatement(newBlockTree, insertIndex, methodCall);
            } else {
                VariableTree methodCall = helper.createMethodSelectionWithReturnValue(wrapper, copy);
                newBlockTree = make.insertBlockStatement(newBlockTree, insertIndex, methodCall);
            }
        }
        copy.rewrite(current, newBlockTree);
    }
}
