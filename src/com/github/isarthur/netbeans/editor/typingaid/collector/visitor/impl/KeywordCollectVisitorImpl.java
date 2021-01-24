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
package com.github.isarthur.netbeans.editor.typingaid.collector.visitor.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.api.Keyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.AssertKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.BreakKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CaseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.CatchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ClassKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ContinueKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.DefaultKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.DoKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ElseKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.EnumKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ExtendsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.FinallyKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ForKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.IfKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ImplementsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ImportKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.InstanceofKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.InterfaceKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.NewKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ReturnKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.StaticKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.SwitchKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.SynchronizedKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThisKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.ThrowsKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.TryKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.VoidKeyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.keyword.impl.WhileKeyword;
import com.github.isarthur.netbeans.editor.typingaid.collector.visitor.api.KeywordCollectVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.BLOCK;
import static com.sun.source.tree.Tree.Kind.CASE;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.COMPILATION_UNIT;
import static com.sun.source.tree.Tree.Kind.DO_WHILE_LOOP;
import static com.sun.source.tree.Tree.Kind.ENHANCED_FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.ENUM;
import static com.sun.source.tree.Tree.Kind.FOR_LOOP;
import static com.sun.source.tree.Tree.Kind.IF;
import static com.sun.source.tree.Tree.Kind.INTERFACE;
import static com.sun.source.tree.Tree.Kind.METHOD;
import static com.sun.source.tree.Tree.Kind.METHOD_INVOCATION;
import static com.sun.source.tree.Tree.Kind.NEW_CLASS;
import static com.sun.source.tree.Tree.Kind.PARENTHESIZED;
import static com.sun.source.tree.Tree.Kind.RETURN;
import static com.sun.source.tree.Tree.Kind.SWITCH;
import static com.sun.source.tree.Tree.Kind.TRY;
import static com.sun.source.tree.Tree.Kind.VARIABLE;
import static com.sun.source.tree.Tree.Kind.WHILE_LOOP;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class KeywordCollectVisitorImpl implements KeywordCollectVisitor {

    @Override
    public void visit(AssertKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(BreakKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (JavaSourceUtilities.getParentTreeOfKind(
                EnumSet.of(CASE, DO_WHILE_LOOP, ENHANCED_FOR_LOOP, FOR_LOOP, SWITCH, WHILE_LOOP),
                request)) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(CaseKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(SWITCH), request)) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(CatchKeyword keyword, CodeCompletionRequest request) {
        collectCatchFinallyKeyword(keyword, request);
    }

    @Override
    public void visit(ClassKeyword keyword, CodeCompletionRequest request) {
        collectClassEnumInterfaceKeyword(keyword, request);
    }

    @Override
    public void visit(ContinueKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (JavaSourceUtilities.getParentTreeOfKind(
                EnumSet.of(DO_WHILE_LOOP, ENHANCED_FOR_LOOP, FOR_LOOP, WHILE_LOOP),
                request)) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(DefaultKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(SWITCH), request)) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(DoKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(ElseKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(IF), request)) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(EnumKeyword keyword, CodeCompletionRequest request) {
        collectClassEnumInterfaceKeyword(keyword, request);
    }

    @Override
    public void visit(ExtendsKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        switch (request.getCurrentKind()) {
            case CLASS:
            case INTERFACE:
                if (JavaSourceUtilities.isPositionOfExtendsKeywordInClassOrInterfaceDeclaration(request)) {
                    codeFragments.add(keyword);
                }
                break;
            case TYPE_PARAMETER:
                codeFragments.add(keyword);
                break;
        }
    }

    @Override
    public void visit(FinallyKeyword keyword, CodeCompletionRequest request) {
        collectCatchFinallyKeyword(keyword, request);
    }

    @Override
    public void visit(ForKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(IfKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(ImplementsKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        Tree.Kind currentContext = JavaSourceUtilities.getCurrentTreeKind(request);
        if (currentContext == null) {
            return;
        }
        switch (currentContext) {
            case CLASS:
            case ENUM:
                Supplier<Boolean> insideImplementsTree = () -> {
                    Tree currentTree = JavaSourceUtilities.getCurrentTree(request);
                    if (currentTree == null) {
                        return false;
                    }
                    WorkingCopy copy = request.getWorkingCopy();
                    TreeUtilities treeUtilities = copy.getTreeUtilities();
                    TokenSequence<JavaTokenId> tokens = treeUtilities.tokensFor(currentTree);
                    tokens.moveStart();
                    int identifierEndOffset = Integer.MAX_VALUE;
                    while (tokens.moveNext()) {
                        if (tokens.token().id() == JavaTokenId.IDENTIFIER) {
                            int endOffset = tokens.offset() + tokens.token().length();
                            if (endOffset <= request.getAbbreviation().getStartOffset()) {
                                identifierEndOffset = endOffset;
                                break;
                            }
                        }
                    }
                    if (identifierEndOffset < request.getAbbreviation().getStartOffset()) {
                        tokens.move(identifierEndOffset);
                        while (tokens.moveNext()) {
                            if (tokens.token().id() == JavaTokenId.LBRACE) {
                                if (request.getAbbreviation().getStartOffset() <= tokens.offset()) {
                                    return true;
                                }
                            } else if (tokens.token().id() != JavaTokenId.WHITESPACE) {
                                return false;
                            }
                        }
                    }
                    return false;
                };
                if (insideImplementsTree.get()) {
                    List<CodeFragment> codeFragments = request.getCodeFragments();
                    codeFragments.add(keyword);
                }
                break;
        }
    }

    @Override
    public void visit(ImportKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getCurrentTreeOfKind(Collections.singleton(COMPILATION_UNIT), request)) {
            List<CodeFragment> codeFragments = request.getCodeFragments();
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(InstanceofKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getCurrentTreeOfKind(EnumSet.of(PARENTHESIZED), request)) {
            WorkingCopy workingCopy = request.getWorkingCopy();
            TokenSequence<?> tokenSequence = workingCopy.getTokenHierarchy().tokenSequence();
            Abbreviation abbreviation = request.getAbbreviation();
            tokenSequence.move(abbreviation.getStartOffset());
            while (tokenSequence.movePrevious() && tokenSequence.token().id() == JavaTokenId.WHITESPACE) {
            }
            Token<?> token = tokenSequence.token();
            if (token != null && token.id() == JavaTokenId.IDENTIFIER) {
                List<CodeFragment> codeFragments = request.getCodeFragments();
                codeFragments.add(keyword);
            }
        }
    }

    @Override
    public void visit(InterfaceKeyword keyword, CodeCompletionRequest request) {
        collectClassEnumInterfaceKeyword(keyword, request);
    }

    @Override
    public void visit(NewKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (!JavaSourceUtilities.getParentTreeOfKind(
                EnumSet.of(ASSIGNMENT, METHOD_INVOCATION, NEW_CLASS, RETURN, VARIABLE),
                request)) {
            return;
        }
        TypeMirror typeInContext = JavaSourceUtilities.getTypeInContext(request);
        if (typeInContext == null) {
            return;
        }
        WorkingCopy copy = request.getWorkingCopy();
        Element type = copy.getTypes().asElement(typeInContext);
        if (type == null) {
            return;
        }
        if (JavaSourceMaker.makeNewClassTree((TypeElement) type, request) == null) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        codeFragments.add(keyword);
    }

    @Override
    public void visit(ReturnKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(METHOD), request)) {
            if (request.getCurrentKind() == BLOCK
                    || request.getCurrentKind() == SWITCH
                    || request.getCurrentKind() == CASE) {
                List<CodeFragment> codeFragments = request.getCodeFragments();
                codeFragments.add(keyword);
            }
        }
    }

    @Override
    public void visit(StaticKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getCurrentTreeOfKind(EnumSet.of(CLASS, ENUM), request)) {
            List<CodeFragment> codeFragments = request.getCodeFragments();
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(SwitchKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(SynchronizedKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(ThisKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        WorkingCopy copy = request.getWorkingCopy();
        Tree.Kind currentContext = JavaSourceUtilities.getCurrentTreeKind(request);
        if (currentContext == null) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        switch (currentContext) {
            case ASSIGNMENT:
            case EQUAL_TO:
            case METHOD_INVOCATION:
            case NEW_CLASS:
            case NOT_EQUAL_TO:
            case PARENTHESIZED:
            case RETURN:
            case VARIABLE:
                codeFragments.add(keyword);
                break;
            case BLOCK:
            case CASE:
                TreePath methodPath = JavaSourceUtilities.getParentPathOfKind(
                        Collections.singleton(METHOD), request);
                if (methodPath == null) {
                    return;
                }
                Supplier<Boolean> parameterHasCorrespondingField = () -> {
                    Trees trees = copy.getTrees();
                    Types types = copy.getTypes();
                    ExecutableElement method = (ExecutableElement) trees.getElement(methodPath);
                    List<? extends VariableElement> parameters = method.getParameters();
                    Element enclosingElement = method.getEnclosingElement();
                    if (enclosingElement.getKind() == ElementKind.CLASS
                            || enclosingElement.getKind() == ElementKind.ENUM) {
                        List<? extends Element> enclosedElements = enclosingElement.getEnclosedElements();
                        List<VariableElement> fields = ElementFilter.fieldsIn(enclosedElements);
                        for (VariableElement field : fields) {
                            for (VariableElement parameter : parameters) {
                                if (types.isAssignable(field.asType(), parameter.asType())) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                };
                if (parameterHasCorrespondingField.get()) {
                    codeFragments.add(keyword);
                }
                break;
        }
    }

    @Override
    public void visit(ThrowKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(ThrowsKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (!JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(METHOD), request)) {
            return;
        }
        Supplier<Boolean> insideThrowsTree = () -> {
            Tree currentTree = JavaSourceUtilities.getCurrentTree(request);
            if (currentTree == null) {
                return false;
            }
            WorkingCopy copy = request.getWorkingCopy();
            TreeUtilities treeUtilities = copy.getTreeUtilities();
            TokenSequence<JavaTokenId> tokens = treeUtilities.tokensFor(currentTree);
            tokens.moveStart();
            int throwsOffset = Integer.MIN_VALUE;
            while (tokens.moveNext()) {
                if (tokens.token().id() == JavaTokenId.THROWS) {
                    if (tokens.offset() < request.getAbbreviation().getStartOffset()) {
                        throwsOffset = tokens.offset();
                        break;
                    }
                }
            }
            if (throwsOffset < request.getAbbreviation().getStartOffset()) {
                tokens.move(throwsOffset);
                while (tokens.moveNext()) {
                    if (tokens.token().id() == JavaTokenId.LBRACE) {
                        if (request.getAbbreviation().getStartOffset() < tokens.offset()) {
                            return true;
                        }
                    } else if (tokens.token().id() != JavaTokenId.WHITESPACE
                            && tokens.token().id() != JavaTokenId.ERROR) {
                        return false;
                    }
                }
            }
            return false;
        };
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (insideThrowsTree.get()) {
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(TryKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    @Override
    public void visit(VoidKeyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getCurrentTreeOfKind(EnumSet.of(CLASS, ENUM, INTERFACE), request)) {
            List<CodeFragment> codeFragments = request.getCodeFragments();
            codeFragments.add(keyword);
        }
    }

    @Override
    public void visit(WhileKeyword keyword, CodeCompletionRequest request) {
        collectExpressionStatementKeyword(keyword, request);
    }

    private void collectExpressionStatementKeyword(Keyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (JavaSourceUtilities.getCurrentTreeOfKind(EnumSet.of(BLOCK, CASE), request)) {
            List<CodeFragment> codeFragments = request.getCodeFragments();
            codeFragments.add(keyword);
        }
    }

    private void collectCatchFinallyKeyword(
            Keyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        if (!JavaSourceUtilities.getParentTreeOfKind(Collections.singleton(TRY), request)) {
            return;
        }
        Tree currentTree = JavaSourceUtilities.getCurrentTree(request);
        if (currentTree == null) {
            return;
        }
        Supplier<JavaTokenId> firstNonWhitespaceTokenId = () -> {
            WorkingCopy copy = request.getWorkingCopy();
            TreeUtilities treeUtilities = copy.getTreeUtilities();
            TokenSequence<JavaTokenId> tokens = treeUtilities.tokensFor(currentTree);
            tokens.move(request.getAbbreviation().getStartOffset());
            while (tokens.moveNext() && tokens.token().id() == JavaTokenId.WHITESPACE) {
            }
            Token<JavaTokenId> token = tokens.token();
            return token == null ? null : token.id();
        };
        Supplier<JavaTokenId> lastNonWhitespaceTokenId = () -> {
            WorkingCopy copy = request.getWorkingCopy();
            TreeUtilities treeUtilities = copy.getTreeUtilities();
            TokenSequence<JavaTokenId> tokens = treeUtilities.tokensFor(currentTree);
            tokens.move(request.getAbbreviation().getStartOffset());
            while (tokens.movePrevious() && tokens.token().id() == JavaTokenId.WHITESPACE) {
            }
            Token<JavaTokenId> token = tokens.token();
            return token == null ? null : token.id();
        };
        JavaTokenId firstNWTokenId = firstNonWhitespaceTokenId.get();
        if (firstNWTokenId == null) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        if (firstNWTokenId == JavaTokenId.CATCH) {
            codeFragments.add(keyword);
        } else if (firstNWTokenId == JavaTokenId.LBRACE) {
            JavaTokenId lastNWTokenId = lastNonWhitespaceTokenId.get();
            if (lastNWTokenId == null) {
                return;
            }
            if (lastNWTokenId == JavaTokenId.TRY) {
                codeFragments.add(keyword);
            }
        }
    }

    private void collectClassEnumInterfaceKeyword(
            Keyword keyword, CodeCompletionRequest request) {
        if (!keyword.isAbbreviationEqualTo(request.getAbbreviation().getContent())) {
            return;
        }
        Tree.Kind currentContext = JavaSourceUtilities.getCurrentTreeKind(request);
        if (currentContext == null) {
            return;
        }
        List<CodeFragment> codeFragments = request.getCodeFragments();
        switch (currentContext) {
            case CLASS:
            case ENUM:
            case INTERFACE:
                Supplier<Boolean> insideBody = () -> {
                    Tree currentTree = JavaSourceUtilities.getCurrentTree(request);
                    if (currentTree == null) {
                        return false;
                    }
                    WorkingCopy copy = request.getWorkingCopy();
                    TreeUtilities treeUtilities = copy.getTreeUtilities();
                    TokenSequence<JavaTokenId> tokens = treeUtilities.tokensFor(currentTree);
                    tokens.moveStart();
                    int leftBraceOffset = Integer.MAX_VALUE;
                    while (tokens.moveNext()) {
                        if (tokens.token().id() == JavaTokenId.LBRACE) {
                            leftBraceOffset = tokens.offset();
                            break;
                        }
                    }
                    return leftBraceOffset < request.getAbbreviation().getStartOffset();
                };
                if (insideBody.get()) {
                    codeFragments.add(keyword);
                }
                break;
            case BLOCK:
                if (keyword.toString().equals("class")) { //NOI18N
                    codeFragments.add(keyword);
                }
                break;
            case COMPILATION_UNIT:
                codeFragments.add(keyword);
                break;
        }
    }
}
