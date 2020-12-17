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
package com.github.isarthur.netbeans.editor.typingaid.collector.linker;

import com.github.isarthur.netbeans.editor.typingaid.collector.ChainedMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ChainedEnumConstantAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ChainedFieldAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;
import com.github.isarthur.netbeans.editor.typingaid.collector.Collector;
import com.github.isarthur.netbeans.editor.typingaid.collector.Collector.Kind;

/**
 *
 * @author Arthur Sadykov
 */
public class ChainedLinker extends Linker {

    public ChainedLinker(Collector collector) {
        super(collector);
    }

    @Override
    public Collector link() {
        if (Preferences.getChainedMethodInvocationFlag()) {
            collectors.add(collector.getKind() == Kind.CHAINED_METHOD_INVOCATION
                    ? collector : new ChainedMethodInvocationCollector());
        }
        if (Preferences.getChainedFieldAccessFlag()) {
            collectors.add(collector.getKind() == Kind.CHAINED_FIELD_ACCESS
                    ? collector : new ChainedFieldAccessCollector());
        }
        if (Preferences.getChainedEnumConstantAccessFlag()) {
            collectors.add(collector.getKind() == Kind.CHAINED_ENUM_CONSTANT_ACCESS
                    ? collector : new ChainedEnumConstantAccessCollector());
        }
        for (int i = 0; i < collectors.size() - 1; i++) {
            collectors.get(i).setNext(collectors.get(i + 1));
        }
        return collectors.isEmpty() ? null : collectors.get(0);
    }
}
