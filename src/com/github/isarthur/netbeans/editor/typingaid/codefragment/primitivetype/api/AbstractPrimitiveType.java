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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.primitivetype.api;

import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractPrimitiveType implements PrimitiveType, Comparable<PrimitiveType> {

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(toString()).equals(abbreviation);
    }

    @Override
    public int compareTo(PrimitiveType other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        WorkingCopy copy = request.getWorkingCopy();
        Types types = copy.getTypes();
        TypeMirror type = types.getPrimitiveType(getTypeKind());
        switch (request.getCurrentKind()) {
            case BLOCK:
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(type, request),
                        JavaSourceMaker.makePrimitiveTypeTree(TypeKind.valueOf(toString().toUpperCase()), request),
                        JavaSourceMaker.makeLiteralTree(getDefaultValue(), request),
                        request);
            case CLASS:
            case ENUM:
                ClassTree classTree = (ClassTree) request.getCurrentTree();
                if (!JavaSourceUtilities.isMethodSection(classTree, request)) {
                    return JavaSourceMaker.makeVariableTree(
                            JavaSourceMaker.makeModifiersTree(Collections.singleton(Modifier.PRIVATE), request),
                            JavaSourceUtilities.getVariableName(type, request),
                            JavaSourceMaker.makePrimitiveTypeTree(TypeKind.valueOf(toString().toUpperCase()), request),
                            null,
                            request);
                } else {
                    return JavaSourceMaker.makeMethodTree(
                            JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                            "method", //NOI18N
                            JavaSourceMaker.makePrimitiveTypeTree(TypeKind.valueOf(toString().toUpperCase()), request),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            JavaSourceMaker.makeBlockTree(
                                    Collections.singletonList(JavaSourceMaker.makeReturnTree(
                                            JavaSourceMaker.makeLiteralTree(getDefaultValue(), request),
                                            request)),
                                    false,
                                    request),
                            null,
                            request);
                }
            case INTERFACE:
                return JavaSourceMaker.makeMethodTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        "method", //NOI18N
                        JavaSourceMaker.makeTypeTree(toString(), request),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        null,
                        request);
            case METHOD:
                return JavaSourceMaker.makeVariableTree(
                        JavaSourceMaker.makeModifiersTree(Collections.emptySet(), request),
                        JavaSourceUtilities.getVariableName(type, request),
                        JavaSourceMaker.makePrimitiveTypeTree(TypeKind.valueOf(toString().toUpperCase()), request),
                        null,
                        request);
            case RETURN:
                ReturnTree returnTree = (ReturnTree) request.getCurrentTree();
                return JavaSourceMaker.makeTypeCastTree(
                        JavaSourceMaker.makePrimitiveTypeTree(getTypeKind(), request),
                        returnTree.getExpression(),
                        request);
            case VARIABLE:
                VariableTree variableTree = (VariableTree) request.getCurrentTree();
                return JavaSourceMaker.makeTypeCastTree(
                        JavaSourceMaker.makePrimitiveTypeTree(getTypeKind(), request),
                        variableTree.getInitializer(),
                        request);
            default:
                return null;
        }
    }
}
