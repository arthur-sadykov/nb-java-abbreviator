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
package com.github.isarthur.netbeans.editor.typingaid.settings;

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
    private static final String FIELD = "field"; //NOI18N
    private static final String PARAMETER = "parameter"; //NOI18N
    private static final String ENUM_CONSTANT = "enumConstant"; //NOI18N
    private static final String EXCEPTION_PARAMETER = "exceptionParameter"; //NOI18N
    private static final String RESOURCE_VARIABLE = "resourceVariable"; //NOI18N
    private static final String INTERNAL_TYPE = "internalType"; //NOI18N
    private static final String EXTERNAL_TYPE = "externalType"; //NOI18N
    private static final String IMPORTED_TYPE = "importedType"; //NOI18N
    private static final String KEYWORD = "keyword"; //NOI18N
    private static final String MODIFIER = "modifier"; //NOI18N
    private static final String PRIMITIVE_TYPE = "primitiveType"; //NOI18N

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

    public static boolean getSettingForField() {
        return NbPreferences.forModule(Settings.class).getBoolean(FIELD, true);
    }

    public static void setSettingForField(boolean field) {
        NbPreferences.forModule(Settings.class).putBoolean(FIELD, field);
    }

    public static boolean getSettingForParameter() {
        return NbPreferences.forModule(Settings.class).getBoolean(PARAMETER, true);
    }

    public static void setSettingForParameter(boolean parameter) {
        NbPreferences.forModule(Settings.class).putBoolean(PARAMETER, parameter);
    }

    public static boolean getSettingForEnumConstant() {
        return NbPreferences.forModule(Settings.class).getBoolean(ENUM_CONSTANT, true);
    }

    public static void setSettingForEnumConstant(boolean enumConstant) {
        NbPreferences.forModule(Settings.class).putBoolean(ENUM_CONSTANT, enumConstant);
    }

    public static boolean getSettingForExceptionParameter() {
        return NbPreferences.forModule(Settings.class).getBoolean(EXCEPTION_PARAMETER, true);
    }

    public static void setSettingForExceptionParameter(boolean exceptionParameter) {
        NbPreferences.forModule(Settings.class).putBoolean(EXCEPTION_PARAMETER, exceptionParameter);
    }

    public static boolean getSettingForResourceVariable() {
        return NbPreferences.forModule(Settings.class).getBoolean(RESOURCE_VARIABLE, true);
    }

    public static void setSettingForResourceVariable(boolean resourceVariable) {
        NbPreferences.forModule(Settings.class).putBoolean(RESOURCE_VARIABLE, resourceVariable);
    }

    public static boolean getSettingForInternalType() {
        return NbPreferences.forModule(Settings.class).getBoolean(INTERNAL_TYPE, true);
    }

    public static void setSettingForInternalType(boolean type) {
        NbPreferences.forModule(Settings.class).putBoolean(INTERNAL_TYPE, type);
    }

    public static boolean getSettingForExternalType() {
        return NbPreferences.forModule(Settings.class).getBoolean(EXTERNAL_TYPE, true);
    }

    public static void setSettingForExternalType(boolean type) {
        NbPreferences.forModule(Settings.class).putBoolean(EXTERNAL_TYPE, type);
    }

    public static boolean getSettingForImportedType() {
        return NbPreferences.forModule(Settings.class).getBoolean(IMPORTED_TYPE, true);
    }

    public static void setSettingForImportedType(boolean type) {
        NbPreferences.forModule(Settings.class).putBoolean(IMPORTED_TYPE, type);
    }

    public static boolean getSettingForKeyword() {
        return NbPreferences.forModule(Settings.class).getBoolean(KEYWORD, true);
    }

    public static void setSettingForKeyword(boolean keyword) {
        NbPreferences.forModule(Settings.class).putBoolean(KEYWORD, keyword);
    }

    public static boolean getSettingForModifier() {
        return NbPreferences.forModule(Settings.class).getBoolean(MODIFIER, true);
    }

    public static void setSettingForModifier(boolean modifier) {
        NbPreferences.forModule(Settings.class).putBoolean(MODIFIER, modifier);
    }

    public static boolean getSettingForPrimitiveType() {
        return NbPreferences.forModule(Settings.class).getBoolean(PRIMITIVE_TYPE, true);
    }

    public static void setSettingForPrimitiveType(boolean primitiveType) {
        NbPreferences.forModule(Settings.class).putBoolean(PRIMITIVE_TYPE, primitiveType);
    }
}
