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

import com.github.isarthur.netbeans.editor.typingaid.collector.Collector;
import com.github.isarthur.netbeans.editor.typingaid.collector.Collector.Kind;
import com.github.isarthur.netbeans.editor.typingaid.collector.EnumConstantCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ExceptionParameterCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ImportedTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.KeywordCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.LocalMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.InternalTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ModifierCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.PrimitiveTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ExternalTypeCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.FieldCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.LocalVariableCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ParameterCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.ResourceVariableCollector;
import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;

/**
 *
 * @author Arthur Sadykov
 */
public class SimpleLinker extends Linker {

    public SimpleLinker(Collector collector) {
        super(collector);
    }

    @Override
    public Collector link() {
        if (Preferences.getLocalVariableFlag()) {
            collectors.add(collector.getKind() == Kind.LOCAL_VARIABLE ? collector : new LocalVariableCollector());
        }
        if (Preferences.getFieldFlag()) {
            collectors.add(collector.getKind() == Kind.FIELD ? collector : new FieldCollector());
        }
        if (Preferences.getParameterFlag()) {
            collectors.add(collector.getKind() == Kind.PARAMETER ? collector : new ParameterCollector());
        }
        if (Preferences.getEnumConstantFlag()) {
            collectors.add(collector.getKind() == Kind.ENUM_CONSTANT ? collector : new EnumConstantCollector());
        }
        if (Preferences.getExceptionParameterFlag()) {
            collectors.add(collector.getKind() == Kind.EXCEPTION_PARAMETER
                    ? collector : new ExceptionParameterCollector());
        }
        if (Preferences.getResourceVariableFlag()) {
            collectors.add(collector.getKind() == Kind.RESOURCE_VARIABLE ? collector : new ResourceVariableCollector());
        }
        if (Preferences.getInternalTypeFlag()) {
            collectors.add(collector.getKind() == Kind.INTERNAL_TYPE ? collector : new InternalTypeCollector());
        }
        if (Preferences.getLocalMethodInvocationFlag()) {
            collectors.add(collector.getKind() == Kind.LOCAL_METHOD_INVOCATION
                    ? collector : new LocalMethodInvocationCollector());
        }
        if (Preferences.getExternalTypeFlag()) {
            collectors.add(collector.getKind() == Kind.EXTERNAL_TYPE ? collector : new ExternalTypeCollector());
        }
        if (Preferences.getImportedTypeFlag()) {
            collectors.add(collector.getKind() == Kind.IMPORTED_TYPE ? collector : new ImportedTypeCollector());
        }
        if (Preferences.getKeywordFlag()) {
            collectors.add(collector.getKind() == Kind.KEYWORD ? collector : new KeywordCollector());
        }
        if (Preferences.getModifierFlag()) {
            collectors.add(collector.getKind() == Kind.MODIFIER ? collector : new ModifierCollector());
        }
        if (Preferences.getPrimitiveTypeFlag()) {
            collectors.add(collector.getKind() == Kind.PRIMITIVE_TYPE ? collector : new PrimitiveTypeCollector());
        }
        for (int i = 0; i < collectors.size() - 1; i++) {
            collectors.get(i).setNext(collectors.get(i + 1));
        }
        return collectors.isEmpty() ? null : collectors.get(0);
    }
}
