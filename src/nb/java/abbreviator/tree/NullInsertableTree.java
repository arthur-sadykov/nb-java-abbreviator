/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class NullInsertableTree extends InsertableTree {

    private static NullInsertableTree instance;

    private NullInsertableTree(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
    }

    public static NullInsertableTree getInstance(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) {
        if (instance == null) {
            instance = new NullInsertableTree(currentPath, wrapper, copy, helper);
        }
        return instance;
    }

    @Override
    public void insert(Tree tree) {
    }
}
