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
package com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl;

import com.github.isarthur.netbeans.editor.typingaid.collector.api.CodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ChainedEnumConstantAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ChainedFieldAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ChainedMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.EnumConstantCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ExceptionParameterCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ExternalThrowableTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ExternalTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.FieldCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.GlobalThrowableTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.GlobalTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.InternalThrowableTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.InternalTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.KeywordCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.LiteralCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.LocalMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.LocalVariableCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.MethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ModifierCollectorFactory;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.NullCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ParameterCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.PrimitiveTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ResourceVariableCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.StaticFieldAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.StaticFieldAccessForGlobalTypesCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.StaticMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.StaticMethodInvocationForGlobalTypesCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.api.CodeFragmentCollectorLinker;
import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Arthur Sadykov
 */
public class CodeFragmentCollectorLinkerImpl implements CodeFragmentCollectorLinker {

    private List<CodeFragmentCollector> collectors = new ArrayList<>();

    private CodeFragmentCollectorLinkerImpl(CodeFragmentCollectorLinkerBuilder builder) {
        this.collectors = builder.collectors;
    }

    public static CodeFragmentCollectorLinkerBuilder builder() {
        return new CodeFragmentCollectorLinkerBuilder();
    }

    @Override
    public CodeFragmentCollector link() {
        for (int i = 0; i < collectors.size() - 1; i++) {
            collectors.get(i).setNext(collectors.get(i + 1));
        }
        return collectors.isEmpty() ? new NullCollector() : collectors.get(0);
    }

    public static class CodeFragmentCollectorLinkerBuilder {

        private List<CodeFragmentCollector> collectors = new ArrayList<>();

        public CodeFragmentCollectorLinkerBuilder linkExternalTypeCollector() {
            if (Preferences.getExternalTypeFlag()) {
                collectors.add(new ExternalTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkExternalThrowableTypeCollector() {
            if (Preferences.getExternalTypeFlag()) {
                collectors.add(new ExternalThrowableTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkLocalVariableCollector() {
            if (Preferences.getLocalVariableFlag()) {
                collectors.add(new LocalVariableCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkFieldCollector() {
            if (Preferences.getFieldFlag()) {
                collectors.add(new FieldCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkParameterCollector() {
            if (Preferences.getParameterFlag()) {
                collectors.add(new ParameterCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkEnumConstantCollector() {
            if (Preferences.getEnumConstantFlag()) {
                collectors.add(new EnumConstantCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkExceptionParameterCollector() {
            if (Preferences.getExceptionParameterFlag()) {
                collectors.add(new ExceptionParameterCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkResourceVariableCollector() {
            if (Preferences.getResourceVariableFlag()) {
                collectors.add(new ResourceVariableCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkInternalTypeCollector() {
            if (Preferences.getInternalTypeFlag()) {
                collectors.add(new InternalTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkInternalThrowableTypeCollector() {
            if (Preferences.getInternalTypeFlag()) {
                collectors.add(new InternalThrowableTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkGlobalTypeCollector() {
            if (Preferences.getGlobalTypeFlag()) {
                collectors.add(new GlobalTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkGlobalThrowableTypeCollector() {
            if (Preferences.getGlobalTypeFlag()) {
                collectors.add(new GlobalThrowableTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkLiteralCollector() {
            if (Preferences.getLiteralFlag()) {
                collectors.add(new LiteralCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkKeywordCollector() {
            if (Preferences.getKeywordFlag()) {
                collectors.add(new KeywordCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkModifierCollector(Tree.Kind kind) {
            if (Preferences.getModifierFlag()) {
                collectors.add(ModifierCollectorFactory.getModifierCollector(kind));
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkPrimitiveTypeCollector() {
            if (Preferences.getPrimitiveTypeFlag()) {
                collectors.add(new PrimitiveTypeCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkLocalMethodInvocationCollector() {
            if (Preferences.getLocalMethodInvocationFlag()) {
                collectors.add(new LocalMethodInvocationCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkChainedMethodInvocationCollector() {
            if (Preferences.getChainedMethodInvocationFlag()) {
                collectors.add(new ChainedMethodInvocationCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkChainedFieldAccessCollector() {
            if (Preferences.getChainedFieldAccessFlag()) {
                collectors.add(new ChainedFieldAccessCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkChainedEnumConstantCollector() {
            if (Preferences.getChainedEnumConstantAccessFlag()) {
                collectors.add(new ChainedEnumConstantAccessCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkMethodInvocationCollector() {
            if (Preferences.getMethodInvocationFlag()) {
                collectors.add(new MethodInvocationCollector());
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkStaticMethodInvocationCollector() {
            if (Preferences.getStaticMethodInvocationFlag()) {
                if (Preferences.getStaticMethodInvocationGlobalTypesFlag()) {
                    collectors.add(new StaticMethodInvocationForGlobalTypesCollector());
                } else {
                    collectors.add(new StaticMethodInvocationCollector());
                }
            }
            return this;
        }

        public CodeFragmentCollectorLinkerBuilder linkStaticFieldAccessCollector() {
            if (Preferences.getStaticFieldAccessFlag()) {
                if (Preferences.getStaticFieldAccessGlobalTypesFlag()) {
                    collectors.add(new StaticFieldAccessForGlobalTypesCollector());
                } else {
                    collectors.add(new StaticFieldAccessCollector());
                }
            }
            return this;
        }

        public CodeFragmentCollectorLinkerImpl build() {
            return new CodeFragmentCollectorLinkerImpl(this);
        }
    }
}
