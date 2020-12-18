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
package com.github.isarthur.netbeans.editor.typingaid.preferences;

import org.openide.util.NbPreferences;

/**
 *
 * @author Arthur Sadykov
 */
public class Preferences {

    private static final String METHOD_INVOCATION = "methodInvocation"; //NOI18N
    private static final String STATIC_METHOD_INVOCATION = "staticMethodInvocation"; //NOI18N
    private static final String CHAINED_METHOD_INVOCATION = "chainedMethodInvocation"; //NOI18N
    private static final String CHAINED_FIELD_ACCESS = "chainedFieldAccess"; //NOI18N
    private static final String CHAINED_ENUM_CONSTANT_ACCESS = "chainedEnumConstantAccess"; //NOI18N
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
    private static final String LITERAL = "literal"; //NOI18N
    private static final String MODIFIER = "modifier"; //NOI18N
    private static final String PRIMITIVE_TYPE = "primitiveType"; //NOI18N
    private static final String SAME_PACKAGE_TYPE = "samePackageType"; //NOI18N
    private static final String STATIC_METHOD_INVOCATION_IMPORTED_TYPES = "staticMethodInvocationImportedTypes"; //NOI18N
    private static final String STATIC_FIELD_ACCESS_IMPORTED_TYPES = "staticFieldAccessImportedTypes"; //NOI18N

    private Preferences() {
    }

    public static boolean getMethodInvocationFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(METHOD_INVOCATION, true);
    }

    public static void setMethodInvocationFlag(boolean methodInvocation) {
        NbPreferences.forModule(Preferences.class).putBoolean(METHOD_INVOCATION, methodInvocation);
    }

    public static boolean getStaticMethodInvocationFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(STATIC_METHOD_INVOCATION, true);
    }

    public static void setStaticMethodInvocationFlag(boolean staticMethodInvocation) {
        NbPreferences.forModule(Preferences.class).putBoolean(STATIC_METHOD_INVOCATION, staticMethodInvocation);
    }

    public static boolean getChainedMethodInvocationFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(CHAINED_METHOD_INVOCATION, true);
    }

    public static void setChainedMethodInvocationFlag(boolean chainedMethodInvocation) {
        NbPreferences.forModule(Preferences.class).putBoolean(CHAINED_METHOD_INVOCATION, chainedMethodInvocation);
    }

    public static boolean getLocalMethodInvocationFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(LOCAL_METHOD_INVOCATION, true);
    }

    public static void setLocalMethodInvocationFlag(boolean localMethodInvocation) {
        NbPreferences.forModule(Preferences.class).putBoolean(LOCAL_METHOD_INVOCATION, localMethodInvocation);
    }

    public static boolean getStaticFieldAccessFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(STATIC_FIELD_ACCESS, true);
    }

    public static void setStaticFieldAccessFlag(boolean staticFieldAccess) {
        NbPreferences.forModule(Preferences.class).putBoolean(STATIC_FIELD_ACCESS, staticFieldAccess);
    }

    public static boolean getLocalVariableFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(LOCAL_VARIABLE, true);
    }

    public static void setLocalVariableFlag(boolean localVariable) {
        NbPreferences.forModule(Preferences.class).putBoolean(LOCAL_VARIABLE, localVariable);
    }

    public static boolean getFieldFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(FIELD, true);
    }

    public static void setFieldFlag(boolean field) {
        NbPreferences.forModule(Preferences.class).putBoolean(FIELD, field);
    }

    public static boolean getParameterFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(PARAMETER, true);
    }

    public static void setParameterFlag(boolean parameter) {
        NbPreferences.forModule(Preferences.class).putBoolean(PARAMETER, parameter);
    }

    public static boolean getEnumConstantFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(ENUM_CONSTANT, true);
    }

    public static void setEnumConstantFlag(boolean enumConstant) {
        NbPreferences.forModule(Preferences.class).putBoolean(ENUM_CONSTANT, enumConstant);
    }

    public static boolean getExceptionParameterFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(EXCEPTION_PARAMETER, true);
    }

    public static void setExceptionParameterFlag(boolean exceptionParameter) {
        NbPreferences.forModule(Preferences.class).putBoolean(EXCEPTION_PARAMETER, exceptionParameter);
    }

    public static boolean getResourceVariableFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(RESOURCE_VARIABLE, true);
    }

    public static void setResourceVariableFlag(boolean resourceVariable) {
        NbPreferences.forModule(Preferences.class).putBoolean(RESOURCE_VARIABLE, resourceVariable);
    }

    public static boolean getInternalTypeFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(INTERNAL_TYPE, true);
    }

    public static void setInternalTypeFlag(boolean type) {
        NbPreferences.forModule(Preferences.class).putBoolean(INTERNAL_TYPE, type);
    }

    public static boolean getExternalTypeFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(EXTERNAL_TYPE, true);
    }

    public static void setExternalTypeFlag(boolean type) {
        NbPreferences.forModule(Preferences.class).putBoolean(EXTERNAL_TYPE, type);
    }

    public static boolean getImportedTypeFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(IMPORTED_TYPE, true);
    }

    public static void setImportedTypeFlag(boolean type) {
        NbPreferences.forModule(Preferences.class).putBoolean(IMPORTED_TYPE, type);
    }

    public static boolean getKeywordFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(KEYWORD, true);
    }

    public static void setKeywordFlag(boolean keyword) {
        NbPreferences.forModule(Preferences.class).putBoolean(KEYWORD, keyword);
    }

    public static boolean getModifierFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(MODIFIER, true);
    }

    public static void setModifierFlag(boolean modifier) {
        NbPreferences.forModule(Preferences.class).putBoolean(MODIFIER, modifier);
    }

    public static boolean getPrimitiveTypeFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(PRIMITIVE_TYPE, true);
    }

    public static void setPrimitiveTypeFlag(boolean primitiveType) {
        NbPreferences.forModule(Preferences.class).putBoolean(PRIMITIVE_TYPE, primitiveType);
    }

    public static boolean getStaticMethodInvocationImportedTypesFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(STATIC_METHOD_INVOCATION_IMPORTED_TYPES, true);
    }

    public static void setStaticMethodInvocationImportedTypesFlag(boolean importedTypes) {
        NbPreferences.forModule(Preferences.class).putBoolean(STATIC_METHOD_INVOCATION_IMPORTED_TYPES, importedTypes);
    }

    public static boolean getStaticFieldAccessImportedTypesFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(STATIC_FIELD_ACCESS_IMPORTED_TYPES, true);
    }

    public static void setStaticFieldAccessImportedTypesFlag(boolean importedTypes) {
        NbPreferences.forModule(Preferences.class).putBoolean(STATIC_FIELD_ACCESS_IMPORTED_TYPES, importedTypes);
    }

    public static boolean getChainedFieldAccessFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(CHAINED_FIELD_ACCESS, true);
    }

    public static void setChainedFieldAccessFlag(boolean chainedFieldAccess) {
        NbPreferences.forModule(Preferences.class).putBoolean(CHAINED_FIELD_ACCESS, chainedFieldAccess);
    }

    public static boolean getChainedEnumConstantAccessFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(CHAINED_ENUM_CONSTANT_ACCESS, true);
    }

    public static void setChainedEnumConstantAccessFlag(boolean chainedEnumConstant) {
        NbPreferences.forModule(Preferences.class).putBoolean(CHAINED_ENUM_CONSTANT_ACCESS, chainedEnumConstant);
    }

    public static boolean getSamePackageTypeFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(SAME_PACKAGE_TYPE, true);
    }

    public static void setSamePackageTypeFlag(boolean samePackageType) {
        NbPreferences.forModule(Preferences.class).putBoolean(SAME_PACKAGE_TYPE, samePackageType);
    }

    public static boolean getLiteralFlag() {
        return NbPreferences.forModule(Preferences.class).getBoolean(LITERAL, true);
    }

    public static void setLiteralFlag(boolean literal) {
        NbPreferences.forModule(Preferences.class).putBoolean(LITERAL, literal);
    }
}
