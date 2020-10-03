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
import com.github.isarthur.netbeans.typingaid.Utilities;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public class Parenthesized extends InsertableExpressionTree {

    private final ParenthesizedTree current;

    public Parenthesized(TreePath currentPath, MethodCall wrapper,
            WorkingCopy copy, JavaSourceHelper helper) {
        super(currentPath, wrapper, copy, helper);
        current = (ParenthesizedTree) currentPath.getLeaf();
    }

    @Override
    public void insert(Tree tree) {
        if (parent == null) {
            return;
        }
        ExpressionTree methodCall = helper.createMethodCallWithoutReturnValue(wrapper);
        ParenthesizedTree parenthesizedTree;
        if (tree != null) {
            String expression = current.getExpression().toString();
            expression = Utilities.createExpression(expression, methodCall);
            parenthesizedTree = make.Parenthesized(make.Identifier(expression));
        } else {
            parenthesizedTree = make.Parenthesized(methodCall);
        }
        parent.insert(parenthesizedTree);
    }
}
