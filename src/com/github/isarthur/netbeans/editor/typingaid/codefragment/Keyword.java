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
import java.util.Set;

/**
 *
 * @author Arthur Sadykov
 */
public class Keyword implements CodeFragment, Comparable<Keyword> {

    private final String name;
    private Set<Tree.Kind> contexts;

    public Keyword(String name) {
        this.name = name;
        this.contexts = new HashSet<>();
        switch (name) {
            case "assert": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "boolean": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "break": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "byte": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "case": //NOI18N
                contexts.add(Tree.Kind.SWITCH);
                break;
            case "char": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "class": //NOI18N
                contexts.add(Tree.Kind.COMPILATION_UNIT);
                contexts.add(Tree.Kind.CLASS);
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "continue": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "default": //NOI18N
                contexts.add(Tree.Kind.SWITCH);
                break;
            case "double": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "else": //NOI18N
                contexts.add(Tree.Kind.IF);
                break;
            case "enum": //NOI18N
                contexts.add(Tree.Kind.COMPILATION_UNIT);
                contexts.add(Tree.Kind.CLASS);
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "extends": //NOI18N
                contexts.add(Tree.Kind.CLASS);
                contexts.add(Tree.Kind.TYPE_PARAMETER);
                break;
            case "false": //NOI18N
                contexts.add(Tree.Kind.ASSIGNMENT);
                break;
            case "finally": //NOI18N
                contexts.add(Tree.Kind.TRY);
                break;
            case "float": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "if": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "instanceof": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "int": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "interface": //NOI18N
                contexts.add(Tree.Kind.COMPILATION_UNIT);
                contexts.add(Tree.Kind.CLASS);
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "implements": //NOI18N
                contexts.add(Tree.Kind.CLASS);
                break;
            case "import": //NOI18N
                contexts.add(Tree.Kind.IMPORT);
                break;
            case "long": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "null": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "Object": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "package": //NOI18N
                contexts.add(Tree.Kind.COMPILATION_UNIT);
                break;
            case "return": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "short": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "String": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "super": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "switch": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "this": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "throw": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
            case "throws": //NOI18N
                contexts.add(Tree.Kind.METHOD);
                break;
            case "true": //NOI18N
                contexts.add(Tree.Kind.VARIABLE);
                break;
            case "void": //NOI18N
                contexts.add(Tree.Kind.METHOD);
                break;
            case "while": //NOI18N
                contexts.add(Tree.Kind.BLOCK);
                break;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAbbreviationEqualTo(String abbreviation) {
        return StringUtilities.getElementAbbreviation(name).equals(abbreviation);
    }

    public boolean isApplicableInContext(Tree.Kind context) {
        return contexts.contains(context);
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
