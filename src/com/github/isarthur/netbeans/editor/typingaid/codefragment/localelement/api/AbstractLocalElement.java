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
package com.github.isarthur.netbeans.editor.typingaid.codefragment.localelement.api;

import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.Tree;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.TypeUtilities;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class AbstractLocalElement implements LocalElement {

    private final Element identifier;

    public AbstractLocalElement(Element identifier) {
        this.identifier = identifier;
    }

    @Override
    public Kind getKind() {
        return Kind.LOCAL_ELEMENT;
    }

    @Override
    public Element getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(toString()).equals(abbreviation);
    }

    @Override
    public Tree getTreeToInsert(CodeCompletionRequest request) {
        switch (request.getCurrentKind()) {
            case AND:
            case AND_ASSIGNMENT:
            case ASSIGNMENT:
            case BITWISE_COMPLEMENT:
            case CONDITIONAL_AND:
            case CONDITIONAL_EXPRESSION:
            case CONDITIONAL_OR:
            case DIVIDE:
            case DIVIDE_ASSIGNMENT:
            case EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LEFT_SHIFT:
            case LEFT_SHIFT_ASSIGNMENT:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
            case LOGICAL_COMPLEMENT:
            case METHOD_INVOCATION:
            case MINUS:
            case MINUS_ASSIGNMENT:
            case MULTIPLY:
            case MULTIPLY_ASSIGNMENT:
            case NEW_CLASS:
            case NOT_EQUAL_TO:
            case PARENTHESIZED:
            case PLUS:
            case PLUS_ASSIGNMENT:
            case PREFIX_DECREMENT:
            case PREFIX_INCREMENT:
            case REMAINDER:
            case REMAINDER_ASSIGNMENT:
            case RETURN:
            case RIGHT_SHIFT:
            case RIGHT_SHIFT_ASSIGNMENT:
            case UNSIGNED_RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            case VARIABLE:
            case XOR:
            case XOR_ASSIGNMENT:
                return JavaSourceMaker.makeIdentifierTree(toString(), request);
            default:
                Element element = getIdentifier();
                TypeMirror typeMirror = element.asType();
                WorkingCopy copy = request.getWorkingCopy();
                CharSequence typeName =
                        copy.getTypeUtilities().getTypeName(typeMirror, TypeUtilities.TypeNameOptions.PRINT_FQN);
                String expression;
                switch (typeName.toString()) {
                    case ConstantDataManager.BYTE:
                    case ConstantDataManager.SHORT:
                    case ConstantDataManager.INT:
                        expression = ConstantDataManager.ZERO;
                        break;
                    case ConstantDataManager.LONG:
                        expression = ConstantDataManager.ZERO_L;
                        break;
                    case ConstantDataManager.FLOAT:
                        expression = ConstantDataManager.ZERO_DOT_ZERO_F;
                        break;
                    case ConstantDataManager.DOUBLE:
                        expression = ConstantDataManager.ZERO_DOT_ZERO;
                        break;
                    case ConstantDataManager.CHAR:
                        expression = ConstantDataManager.EMPTY_CHAR;
                        break;
                    case ConstantDataManager.BOOLEAN:
                        expression = ConstantDataManager.TRUE;
                        break;
                    case ConstantDataManager.STRING:
                        expression = ConstantDataManager.EMPTY_STRING;
                        break;
                    default:
                        expression = ConstantDataManager.NULL;
                }
                AssignmentTree assignmentTree = JavaSourceMaker.makeAssignmentTree(
                        JavaSourceMaker.makeIdentifierTree(toString(), request),
                        JavaSourceMaker.makeIdentifierTree(expression, request),
                        request);
                copy.tag(assignmentTree, ConstantDataManager.SECOND_IDENTIFIER_OR_LITERAL_TAG);
                return JavaSourceMaker.makeExpressionStatementTree(assignmentTree, request);
        }
    }

    @Override
    public String toString() {
        return identifier.getSimpleName().toString();
    }
}
