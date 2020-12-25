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
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import static com.sun.source.tree.Tree.Kind.METHOD;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class ClassModifierCollector extends ModifierCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Abbreviation abbreviation = request.getAbbreviation();
        TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
        tokens.move(abbreviation.getStartOffset());
        while (tokens.movePrevious() && tokens.token().id() == JavaTokenId.WHITESPACE) {
        }
        Token<?> token = tokens.token();
        if (token != null && JavaSourceUtilities.isModifier(token.id())) {
            TreePath currentPath = request.getCurrentPath();
            TreePath parentPath = currentPath.getParentPath();
            if (parentPath != null) {
                ClassTree classTree = (ClassTree) currentPath.getLeaf();
                ModifiersTree modifiersTree = classTree.getModifiers();
                switch (parentPath.getLeaf().getKind()) {
                    case BLOCK:
                        collectMethodLocalInnerClassModifiers(modifiersTree, request);
                        break;
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                        collectInnerClassModifiers(modifiersTree, request);
                        break;
                    case COMPILATION_UNIT:
                        collectTopLevelClassModifiers(modifiersTree, request);
                        break;
                }
            }
        } else {
            tokens.move(abbreviation.getStartOffset());
            while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
            }
            while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
            }
            TreeUtilities treeUtilities = copy.getTreeUtilities();
            TreePath path = treeUtilities.getPathElementOfKind(
                    EnumSet.of(CLASS, ENUM, INTERFACE, METHOD, VARIABLE),
                    treeUtilities.pathFor(tokens.offset()));
            if (path != null) {
                ClassTree classTree;
                ModifiersTree modifiersTree;
                switch (path.getLeaf().getKind()) {
                    case CLASS:
                        classTree = (ClassTree) path.getLeaf();
                        modifiersTree = classTree.getModifiers();
                        collectInnerClassModifiers(modifiersTree, request);
                        break;
                    case ENUM:
                        classTree = (ClassTree) path.getLeaf();
                        modifiersTree = classTree.getModifiers();
                        collectInnerEnumModifiers(modifiersTree, request);
                        break;
                    case INTERFACE:
                        classTree = (ClassTree) path.getLeaf();
                        modifiersTree = classTree.getModifiers();
                        collectInnerInterfaceModifiers(modifiersTree, request);
                        break;
                    case METHOD:
                        MethodTree methodTree = (MethodTree) path.getLeaf();
                        modifiersTree = methodTree.getModifiers();
                        collectMethodModifiers(modifiersTree, request);
                        break;
                    case VARIABLE:
                        VariableTree variableTree = (VariableTree) path.getLeaf();
                        modifiersTree = variableTree.getModifiers();
                        collectFieldModifiers(modifiersTree, request);
                        break;
                }
            }
        }
        super.collect(request);
    }
}
