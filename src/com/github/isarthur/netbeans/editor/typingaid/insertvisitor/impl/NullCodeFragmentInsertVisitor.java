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
package com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl;

import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.CodeFragmentInsertVisitor;

/**
 *
 * @author Arthur Sadykov
 */
public class NullCodeFragmentInsertVisitor implements CodeFragmentInsertVisitor {

    private static NullCodeFragmentInsertVisitor instance;

    private NullCodeFragmentInsertVisitor() {
    }

    public static NullCodeFragmentInsertVisitor getInstance() {
        if (instance == null) {
            instance = new NullCodeFragmentInsertVisitor();
        }
        return instance;
    }
}
