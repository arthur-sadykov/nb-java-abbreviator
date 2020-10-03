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
import com.github.isarthur.nb.java.abbreviator.codefragment.MethodCall;
import com.github.isarthur.nb.java.abbreviator.Utilities;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class EnhancedFor extends InsertableStatementTree {

    private final EnhancedForLoopTree current;

    public EnhancedFor(TreePath currentPath, MethodCall wrapper,
            WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (EnhancedForLoopTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodCallWithoutReturnValue(wrapper);
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
