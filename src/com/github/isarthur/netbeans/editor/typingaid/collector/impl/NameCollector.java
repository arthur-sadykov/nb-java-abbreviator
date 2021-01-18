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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.name.impl.NameImpl;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.LocalElementCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.Utilities;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.lexer.JavaTokenId;
import static org.netbeans.api.java.lexer.JavaTokenId.BOOLEAN;
import static org.netbeans.api.java.lexer.JavaTokenId.BYTE;
import static org.netbeans.api.java.lexer.JavaTokenId.CHAR;
import static org.netbeans.api.java.lexer.JavaTokenId.DOUBLE;
import static org.netbeans.api.java.lexer.JavaTokenId.FLOAT;
import static org.netbeans.api.java.lexer.JavaTokenId.IDENTIFIER;
import static org.netbeans.api.java.lexer.JavaTokenId.INT;
import static org.netbeans.api.java.lexer.JavaTokenId.LONG;
import static org.netbeans.api.java.lexer.JavaTokenId.SHORT;
import org.netbeans.api.java.source.CodeStyle;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class NameCollector extends LocalElementCollector {

    @Override
    public void collect(CodeCompletionRequest request) {
        TreePath currentTreePath = request.getCurrentPath();
        TreePath parentPath = currentTreePath.getParentPath();
        if (parentPath == null) {
            return;
        }
        if (parentPath.getLeaf().getKind() == Tree.Kind.CATCH) {
            return;
        }
        WorkingCopy workingCopy = request.getWorkingCopy();
        Tree currentTree = request.getCurrentTree();
        TreeUtilities treeUtilities = workingCopy.getTreeUtilities();
        TokenSequence<JavaTokenId> tokenSequence = treeUtilities.tokensFor(currentTree);
        tokenSequence.moveStart();
        int typeCount = 0;
        Abbreviation abbreviation = request.getAbbreviation();
        Set<JavaTokenId> typeTokenIds = EnumSet.of(BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, IDENTIFIER, INT, LONG, SHORT);
        while (tokenSequence.moveNext() && tokenSequence.offset() < abbreviation.getStartOffset()) {
            if (typeTokenIds.contains(tokenSequence.token().id())) {
                typeCount++;
            }
        }
        if (typeCount == 1) {
            TypeMirror type = JavaSourceUtilities.getTypeInContext(request);
            Set<String> variableNames = getVariableNames(type, request);
            List<CodeFragment> codeFragments = request.getCodeFragments();
            variableNames.stream()
                    .filter(name -> StringUtilities.getElementAbbreviation(name).equals(abbreviation.getContent()))
                    .forEach(name -> codeFragments.add(new NameImpl(name)));
        }
        super.collect(request);
    }

    private Set<String> getVariableNames(TypeMirror type, CodeCompletionRequest request) {
        Set<String> names = new HashSet<>();
        WorkingCopy workingCopy = request.getWorkingCopy();
        List<Element> localElements = collectLocalElements(request, ElementKind.FIELD);
        Iterator<String> nameSuggestions = Utilities.varNamesSuggestions(type, ElementKind.FIELD,
                Collections.emptySet(), null, null, workingCopy.getTypes(), workingCopy.getElements(), localElements,
                CodeStyle.getDefault(request.getComponent().getDocument())).iterator();
        while (nameSuggestions.hasNext()) {
            names.add(nameSuggestions.next());
        }
        return Collections.unmodifiableSet(names);
    }
}
