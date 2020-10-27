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
package com.github.isarthur.netbeans.editor.typingaid;

import com.github.isarthur.netbeans.editor.typingaid.spi.Abbreviation;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaAbbreviation implements Abbreviation {

    private static JavaAbbreviation instance;
    private int startOffset;
    private final StringBuffer buffer;

    private JavaAbbreviation() {
        this.startOffset = -1;
        this.buffer = new StringBuffer();
    }

    public static JavaAbbreviation getInstance() {
        if (instance == null) {
            instance = new JavaAbbreviation();
        }
        return instance;
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    @Override
    public int getEndOffset() {
        return startOffset + buffer.length();
    }

    @Override
    public JavaAbbreviation append(char character) {
        buffer.append(character);
        return this;
    }

    @Override
    public void reset() {
        buffer.setLength(0);
        startOffset = -1;
    }

    @Override
    public boolean isEmpty() {
        return buffer.length() == 0;
    }

    @Override
    public String getContent() {
        return buffer.toString();
    }

    @Override
    public void delete() {
        if (!isEmpty()) {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
    }

    @Override
    public int length() {
        return buffer.length();
    }
}
