/*
 * Copyright 2020 Arthur Sadykov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.isarthur.netbeans.typingaid.tree;

import com.github.isarthur.netbeans.typingaid.JavaSourceHelper;
import com.github.isarthur.netbeans.typingaid.codefragment.MethodCall;
import com.github.isarthur.netbeans.typingaid.TreeFactory;
import com.github.isarthur.netbeans.typingaid.constants.ConstantDataManager;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class InsertableTree {

    protected static final Set<Tree.Kind> KINDS = EnumSet.of(
            Tree.Kind.IF,
            Tree.Kind.WHILE_LOOP,
            Tree.Kind.RETURN,
            Tree.Kind.BLOCK,
            Tree.Kind.VARIABLE,
            Tree.Kind.ASSIGNMENT,
            Tree.Kind.METHOD_INVOCATION);
    protected final TreePath currentPath;
    protected final MethodCall wrapper;
    protected final TreeMaker make;
    protected final WorkingCopy copy;
    protected final JavaSourceHelper helper;
    protected final TreeUtilities treeUtilities;
    protected InsertableTree parent;

    protected InsertableTree(TreePath currentPath, MethodCall wrapper, WorkingCopy copy,
            JavaSourceHelper helper) {
        requireNonNull(currentPath, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "currentPath"));
        requireNonNull(wrapper, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "wrapper"));
        requireNonNull(copy, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "copy"));
        requireNonNull(helper, () -> String.format(ConstantDataManager.ARGUMENT_MUST_BE_NON_NULL, "helper"));
        this.currentPath = currentPath;
        this.wrapper = wrapper;
        this.copy = copy;
        this.helper = helper;
        make = copy.getTreeMaker();
        treeUtilities = copy.getTreeUtilities();
        TreePath parentPath = currentPath.getParentPath();
        if (parentPath != null) {
            parent = TreeFactory.create(parentPath, wrapper, copy, helper);
        }
    }

    public abstract void insert(Tree tree);
}
