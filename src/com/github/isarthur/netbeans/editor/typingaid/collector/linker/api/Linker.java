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
package com.github.isarthur.netbeans.editor.typingaid.collector.linker.api;

import com.github.isarthur.netbeans.editor.typingaid.collector.api.Collector;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Parameters;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class Linker {

    protected List<Collector> collectors = new ArrayList<>();
    protected Collector collector;

    protected Linker(Collector collector) {
        Parameters.notNull("collector", collector); //NOI18N
        this.collector = collector;
    }

    public abstract Collector link();
}
