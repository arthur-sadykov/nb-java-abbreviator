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
package com.github.isarthur.netbeans.typingaid;

/**
 *
 * @author Arthur Sadykov
 */
public class Abbreviation {

    private static Abbreviation instance;
    private int endPosition;
    private final StringBuffer buffer;

    private Abbreviation() {
        this.endPosition = -1;
        this.buffer = new StringBuffer();
    }

    static Abbreviation getInstance() {
        if (instance == null) {
            instance = new Abbreviation();
        }
        return instance;
    }

    int getStartPosition() {
        return endPosition - buffer.length();
    }

    void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    int getEndPosition() {
        return endPosition;
    }

    void append(char character) {
        buffer.append(character);
    }

    void reset() {
        buffer.setLength(0);
        endPosition = -1;
    }

    boolean isEmpty() {
        return buffer.length() == 0;
    }

    String getContent() {
        return buffer.toString();
    }

    void delete() {
        if (!isEmpty()) {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
    }

    int length() {
        return buffer.length();
    }
}
