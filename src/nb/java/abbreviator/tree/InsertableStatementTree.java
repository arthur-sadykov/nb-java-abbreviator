/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.tree;

import com.sun.source.util.TreePath;
import nb.java.abbreviator.JavaSourceHelper;
import nb.java.abbreviator.MethodSelectionWrapper;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class InsertableStatementTree extends InsertableTree {

    public InsertableStatementTree(TreePath currentPath, MethodSelectionWrapper wrapper, WorkingCopy copy,
            JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
    }
}
