/*
 * Copyright 2021 Arthur Sadykov.
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
package com.github.isarthur.netbeans.editor.typingaid.collector.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.ModifierCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class ModifiersModifierCollector extends ModifierCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        TreeUtilities treeUtilities = copy.getTreeUtilities();
        TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
        Abbreviation abbreviation = request.getAbbreviation();
        tokens.move(abbreviation.getStartOffset());
        while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        TreePath path = treeUtilities.getPathElementOfKind(
                EnumSet.of(Tree.Kind.VARIABLE, Tree.Kind.METHOD, Tree.Kind.CLASS, Tree.Kind.INTERFACE,
                        Tree.Kind.ENUM),
                request.getCurrentPath());
        if (path != null) {
            ModifiersTree modifiersTree;
            TreePath parentPath;
            switch (path.getLeaf().getKind()) {
                case CLASS:
                    ClassTree clazz = (ClassTree) path.getLeaf();
                    modifiersTree = clazz.getModifiers();
                    parentPath = path.getParentPath();
                    if (parentPath != null) {
                        Tree.Kind parentKind = parentPath.getLeaf().getKind();
                        switch (parentKind) {
                            case BLOCK:
                                collectMethodLocalInnerClassModifiers(modifiersTree, request);
                                break;
                            case COMPILATION_UNIT:
                                collectTopLevelClassModifiers(modifiersTree, request);
                                break;
                            default:
                                collectInnerClassModifiers(modifiersTree, request);
                        }
                    }
                    break;
                case ENUM:
                    ClassTree enumeration = (ClassTree) path.getLeaf();
                    modifiersTree = enumeration.getModifiers();
                    parentPath = path.getParentPath();
                    if (parentPath != null) {
                        Tree.Kind parentKind = parentPath.getLeaf().getKind();
                        switch (parentKind) {
                            case COMPILATION_UNIT:
                                collectTopLevelEnumModifiers(modifiersTree, request);
                                break;
                            default:
                                collectInnerEnumModifiers(modifiersTree, request);
                        }
                    }
                    break;
                case INTERFACE:
                    ClassTree interfaze = (ClassTree) path.getLeaf();
                    modifiersTree = interfaze.getModifiers();
                    parentPath = path.getParentPath();
                    if (parentPath != null) {
                        Tree.Kind parentKind = parentPath.getLeaf().getKind();
                        switch (parentKind) {
                            case COMPILATION_UNIT:
                                collectTopLevelInterfaceModifiers(modifiersTree, request);
                                break;
                            default:
                                collectInnerInterfaceModifiers(modifiersTree, request);
                        }
                    }
                    break;
                case METHOD:
                    MethodTree method = (MethodTree) path.getLeaf();
                    modifiersTree = method.getModifiers();
                    collectMethodModifiers(modifiersTree, request);
                    break;
                case VARIABLE:
                    VariableTree variable = (VariableTree) path.getLeaf();
                    modifiersTree = variable.getModifiers();
                    collectFieldModifiers(modifiersTree, request);
                    break;
            }
        }
        super.collect(request);
    }
}
