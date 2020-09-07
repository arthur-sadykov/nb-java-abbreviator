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
package com.github.isarthur.nb.java.abbreviator.tree;

import com.github.isarthur.nb.java.abbreviator.JavaSourceHelper;
import com.github.isarthur.nb.java.abbreviator.MethodSelectionWrapper;
import com.github.isarthur.nb.java.abbreviator.Utilities;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
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
