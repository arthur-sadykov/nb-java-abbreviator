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
package com.github.isarthur.netbeans.editor.typingaid.codefragment;

import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.Tree;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Arthur Sadykov
 */
public class Keyword implements CodeFragment, Comparable<Keyword> {

    private final String name;
    private Set<Tree.Kind> allowedContexts;
    private Set<Tree.Kind> forbiddenContexts;

    public Keyword(String name) {
        this.name = name;
        this.allowedContexts = new HashSet<>();
        this.forbiddenContexts = new HashSet<>();
        switch (name) {
            case "assert": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "boolean": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "break": //NOI18N
                allowedContexts.add(Tree.Kind.DO_WHILE_LOOP);
                allowedContexts.add(Tree.Kind.FOR_LOOP);
                allowedContexts.add(Tree.Kind.SWITCH);
                allowedContexts.add(Tree.Kind.WHILE_LOOP);
                break;
            case "byte": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "case": //NOI18N
                allowedContexts.add(Tree.Kind.SWITCH);
                break;
            case "catch": //NOI18N
                allowedContexts.add(Tree.Kind.TRY);
                break;
            case "char": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "class": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                allowedContexts.add(Tree.Kind.CLASS);
                allowedContexts.add(Tree.Kind.COMPILATION_UNIT);
                allowedContexts.add(Tree.Kind.ENUM);
                allowedContexts.add(Tree.Kind.INTERFACE);
                forbiddenContexts.add(Tree.Kind.TRY);
                forbiddenContexts.add(Tree.Kind.CATCH);
                break;
            case "continue": //NOI18N
                allowedContexts.add(Tree.Kind.DO_WHILE_LOOP);
                allowedContexts.add(Tree.Kind.FOR_LOOP);
                allowedContexts.add(Tree.Kind.WHILE_LOOP);
                break;
            case "default": //NOI18N
                allowedContexts.add(Tree.Kind.SWITCH);
                break;
            case "do": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "double": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "else": //NOI18N
                allowedContexts.add(Tree.Kind.IF);
                break;
            case "enum": //NOI18N
                allowedContexts.add(Tree.Kind.COMPILATION_UNIT);
                allowedContexts.add(Tree.Kind.CLASS);
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "extends": //NOI18N
                allowedContexts.add(Tree.Kind.CLASS);
                allowedContexts.add(Tree.Kind.TYPE_PARAMETER);
                break;
            case "false": //NOI18N
                allowedContexts.add(Tree.Kind.ASSIGNMENT);
                break;
            case "finally": //NOI18N
                allowedContexts.add(Tree.Kind.TRY);
                break;
            case "float": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "for": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                forbiddenContexts.add(Tree.Kind.TRY);
                forbiddenContexts.add(Tree.Kind.CATCH);
                break;
            case "if": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "instanceof": //NOI18N
                forbiddenContexts.add(Tree.Kind.BLOCK);
                break;
            case "int": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "interface": //NOI18N
                allowedContexts.add(Tree.Kind.CLASS);
                allowedContexts.add(Tree.Kind.COMPILATION_UNIT);
                allowedContexts.add(Tree.Kind.ENUM);
                allowedContexts.add(Tree.Kind.INTERFACE);
                forbiddenContexts.add(Tree.Kind.BLOCK);
                break;
            case "implements": //NOI18N
                allowedContexts.add(Tree.Kind.CLASS);
                allowedContexts.add(Tree.Kind.ENUM);
                forbiddenContexts.add(Tree.Kind.BLOCK);
                break;
            case "import": //NOI18N
                allowedContexts.add(Tree.Kind.IMPORT);
                break;
            case "long": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "null": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "Object": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "package": //NOI18N
                allowedContexts.add(Tree.Kind.COMPILATION_UNIT);
                break;
            case "return": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "short": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "String": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "super": //NOI18N
                allowedContexts.add(Tree.Kind.OTHER);
                break;
            case "switch": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "this": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "throw": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "throws": //NOI18N
                allowedContexts.add(Tree.Kind.THROW);
                break;
            case "true": //NOI18N
                allowedContexts.add(Tree.Kind.VARIABLE);
                break;
            case "try": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
            case "void": //NOI18N
                allowedContexts.add(Tree.Kind.METHOD);
                break;
            case "while": //NOI18N
                allowedContexts.add(Tree.Kind.BLOCK);
                break;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(name).equals(abbreviation);
    }

    public boolean isApplicableInContexts(List<Tree.Kind> contexts) {
        return contexts.stream().anyMatch(context -> this.allowedContexts.contains(context));
    }

    public boolean isForbiddenInContexts(List<Tree.Kind> contexts) {
        return contexts.stream().anyMatch(context -> this.forbiddenContexts.contains(context));
    }

    @Override
    public Kind getKind() {
        return Kind.KEYWORD;
    }

    @Override
    public int compareTo(Keyword other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return name;
    }
}
