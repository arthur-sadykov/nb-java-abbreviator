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
package com.github.isarthur.netbeans.editor.typingaid.util;

import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;

/**
 *
 * @author Arthur Sadykov
 */
public class StringUtilities {

    private StringUtilities() {
    }

    public static String getElementAbbreviation(String elementName) {
        if (elementName.isEmpty()) {
            return ConstantDataManager.EMPTY_STRING;
        }
        StringBuilder abbreviation = new StringBuilder();
        abbreviation.append(Character.toLowerCase(elementName.charAt(0)));
        if (elementName.matches("^[A-Z][A-Z]*(_([A-Z])[A-Z]*)*$")) { //NOI18N
            char previous = elementName.charAt(0);
            for (int i = 1; i < elementName.length(); i++) {
                if (previous == '_') {
                    abbreviation.append(Character.toLowerCase(elementName.charAt(i)));
                }
                previous = elementName.charAt(i);
            }
        } else {
            for (int i = 1; i < elementName.length(); i++) {
                if (Character.isUpperCase(elementName.charAt(i))) {
                    abbreviation.append(Character.toLowerCase(elementName.charAt(i)));
                }
            }
        }
        return abbreviation.toString();
    }

    public static String getMethodAbbreviation(String methodName) {
        StringBuilder abbreviation = new StringBuilder().append(Character.toLowerCase(methodName.charAt(0)));
        for (int i = 1; i < methodName.length(); i++) {
            if (Character.isUpperCase(methodName.charAt(i)) && Character.isLowerCase(methodName.charAt(i - 1))) {
                abbreviation.append(Character.toLowerCase(methodName.charAt(i)));
            }
        }
        return abbreviation.toString();
    }
}
