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
package com.github.isarthur.netbeans.editor.typingaid.tree;

import com.github.isarthur.netbeans.editor.typingaid.JavaSourceHelper;
import com.github.isarthur.netbeans.editor.typingaid.TreeFactory;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.MethodCall;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class InsertableExpressionTree extends InsertableTree {

    private final ExpressionTree current;

    public InsertableExpressionTree(TreePath currentPath, MethodCall methodCall, WorkingCopy copy,
            JavaSourceHelper helper, int position) {
        super(currentPath, methodCall, copy, helper, position);
        current = (ExpressionTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        TreePath parentPath = treeUtilities.getPathElementOfKind(
                EnumSet.of(
                        Tree.Kind.ASSERT,
                        Tree.Kind.ASSIGNMENT,
                        Tree.Kind.EXPRESSION_STATEMENT,
                        Tree.Kind.ENHANCED_FOR_LOOP,
                        Tree.Kind.FOR_LOOP,
                        Tree.Kind.METHOD_INVOCATION,
                        Tree.Kind.PARENTHESIZED,
                        Tree.Kind.RETURN,
                        Tree.Kind.VARIABLE),
                currentPath);
        if (parentPath == null) {
            return;
        }
        parent = TreeFactory.create(parentPath, methodCall, copy, helper, position);
        parent.insert(current);
    }
}