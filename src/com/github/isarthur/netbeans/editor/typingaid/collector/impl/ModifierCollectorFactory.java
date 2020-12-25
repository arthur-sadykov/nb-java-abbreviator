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

import com.github.isarthur.netbeans.editor.typingaid.collector.api.ModifierCollector;
import com.sun.source.tree.Tree;

/**
 *
 * @author Arthur Sadykov
 */
public class ModifierCollectorFactory {

    public static ModifierCollector getModifierCollector(Tree.Kind kind) {
        switch (kind) {
            case BLOCK:
                return new BlockModifierCollector();
            case CLASS:
                return new ClassModifierCollector();
            case COMPILATION_UNIT:
                return new CompilationUnitModifierCollector();
            case ENUM:
                return new EnumModifierCollector();
            case INTERFACE:
                return new InterfaceModifierCollector();
            case MODIFIERS:
                return new ModifiersModifierCollector();
            case METHOD:
                return new MethodModifierCollector();
            case VARIABLE:
                return new VariableModifierCollector();
            default:
                return new NullModifierCollector();
        }
    }
}
