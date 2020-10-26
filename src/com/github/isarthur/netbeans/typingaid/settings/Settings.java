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
package com.github.isarthur.netbeans.typingaid.settings;

import org.openide.util.NbPreferences;

/**
 *
 * @author Arthur Sadykov
 */
public class Settings {

    private static final String METHOD_INVOCATION = "methodInvocation"; //NOI18N
    private static final String STATIC_METHOD_INVOCATION = "staticMethodInvocation"; //NOI18N
    private static final String CHAINED_METHOD_INVOCATION = "chainedMethodInvocation"; //NOI18N
    private static final String LOCAL_METHOD_INVOCATION = "localMethodInvocation"; //NOI18N
    private static final String STATIC_FIELD_ACCESS = "staticFieldAccess"; //NOI18N
    private static final String LOCAL_VARIABLE = "localVariable"; //NOI18N
    private static final String TYPE = "type"; //NOI18N
    private static final String KEYWORD = "keyword"; //NOI18N

    private Settings() {
    }

    public static boolean getSettingForMethodInvocation() {
        return NbPreferences.forModule(Settings.class).getBoolean(METHOD_INVOCATION, true);
    }

    public static void setSettingForMethodInvocation(boolean methodInvocation) {
        NbPreferences.forModule(Settings.class).putBoolean(METHOD_INVOCATION, methodInvocation);
    }

    public static boolean getSettingForStaticMethodInvocation() {
        return NbPreferences.forModule(Settings.class).getBoolean(STATIC_METHOD_INVOCATION, true);
    }

    public static void setSettingForStaticMethodInvocation(boolean staticMethodInvocation) {
        NbPreferences.forModule(Settings.class).putBoolean(STATIC_METHOD_INVOCATION, staticMethodInvocation);
    }

    public static boolean getSettingForChainedMethodInvocation() {
        return NbPreferences.forModule(Settings.class).getBoolean(CHAINED_METHOD_INVOCATION, true);
    }

    public static void setSettingForChainedMethodInvocation(boolean chainedMethodInvocation) {
        NbPreferences.forModule(Settings.class).putBoolean(CHAINED_METHOD_INVOCATION, chainedMethodInvocation);
    }

    public static boolean getSettingForLocalMethodInvocation() {
        return NbPreferences.forModule(Settings.class).getBoolean(LOCAL_METHOD_INVOCATION, true);
    }

    public static void setSettingForLocalMethodInvocation(boolean localMethodInvocation) {
        NbPreferences.forModule(Settings.class).putBoolean(LOCAL_METHOD_INVOCATION, localMethodInvocation);
    }

    public static boolean getSettingForStaticFieldAccess() {
        return NbPreferences.forModule(Settings.class).getBoolean(STATIC_FIELD_ACCESS, true);
    }

    public static void setSettingForStaticFieldAccess(boolean staticFieldAccess) {
        NbPreferences.forModule(Settings.class).putBoolean(STATIC_FIELD_ACCESS, staticFieldAccess);
    }

    public static boolean getSettingForLocalVariable() {
        return NbPreferences.forModule(Settings.class).getBoolean(LOCAL_VARIABLE, true);
    }

    public static void setSettingForLocalVariable(boolean localVariable) {
        NbPreferences.forModule(Settings.class).putBoolean(LOCAL_VARIABLE, localVariable);
    }

    public static boolean getSettingForType() {
        return NbPreferences.forModule(Settings.class).getBoolean(TYPE, true);
    }

    public static void setSettingForType(boolean type) {
        NbPreferences.forModule(Settings.class).putBoolean(TYPE, type);
    }

    public static boolean getSettingForKeyword() {
        return NbPreferences.forModule(Settings.class).getBoolean(KEYWORD, true);
    }

    public static void setSettingForKeyword(boolean keyword) {
        NbPreferences.forModule(Settings.class).putBoolean(KEYWORD, keyword);
    }
}
