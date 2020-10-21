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

import com.github.isarthur.netbeans.typingaid.constants.ConstantDataManager;
import com.sun.source.tree.ExpressionTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.CodeStyle;
import org.netbeans.api.java.source.CodeStyleUtils;

/**
 *
 * @author Arthur Sadykov
 */
public class Utilities {

    private Utilities() {
    }

    public static List<String> varNamesSuggestions(TypeMirror type, ElementKind kind, Set<Modifier> modifiers,
            String suggestedName, String prefix, Types types, Elements elements, Iterable<? extends Element> locals,
            CodeStyle codeStyle) {
        List<String> result = new ArrayList<>();
        if (type == null && suggestedName == null) {
            return result;
        }
        Collection<String> vnct;
        if (suggestedName != null) {
            vnct = new LinkedHashSet<>();
            vnct.add(suggestedName);
            if (type != null) {
                vnct.addAll(varNamesForType(type, types, elements, prefix));
            }
        } else {
            vnct = varNamesForType(type, types, elements, prefix);
        }
        boolean isConst = false;
        String namePrefix = null;
        String nameSuffix = null;
        switch (kind) {
            case FIELD:
                if (modifiers.contains(Modifier.STATIC)) {
                    if (codeStyle != null) {
                        namePrefix = codeStyle.getStaticFieldNamePrefix();
                        nameSuffix = codeStyle.getStaticFieldNameSuffix();
                    }
                    isConst = modifiers.contains(Modifier.FINAL);
                } else {
                    if (codeStyle != null) {
                        namePrefix = codeStyle.getFieldNamePrefix();
                        nameSuffix = codeStyle.getFieldNameSuffix();
                    }
                }
                break;
            case LOCAL_VARIABLE:
            case EXCEPTION_PARAMETER:
            case RESOURCE_VARIABLE:
                if (codeStyle != null) {
                    namePrefix = codeStyle.getLocalVarNamePrefix();
                    nameSuffix = codeStyle.getLocalVarNameSuffix();
                }
                break;
            case PARAMETER:
                if (codeStyle != null) {
                    namePrefix = codeStyle.getParameterNamePrefix();
                    nameSuffix = codeStyle.getParameterNameSuffix();
                }
                break;
        }
        if (isConst) {
            List<String> ls = new ArrayList<>(vnct.size());
            for (String s : vnct) {
                ls.add(getConstName(s));
            }
            vnct = ls;
        }
        if (vnct.isEmpty() && prefix != null && prefix.length() > 0
                && (namePrefix != null && namePrefix.length() > 0
                || nameSuffix != null && nameSuffix.length() > 0)) {
            vnct = Collections.singletonList(prefix);
        }
        String p = prefix;
        while (p != null && p.length() > 0) {
            List<String> l = new ArrayList<>();
            for (String name : vnct) {
                if (startsWith(name, p)) {
                    l.add(name);
                }
            }
            if (l.isEmpty()) {
                p = nextName(p);
            } else {
                vnct = l;
                if (prefix != null) {
                    prefix = prefix.substring(0, prefix.length() - p.length());
                }
                p = null;
            }
        }
        for (String name : vnct) {
            boolean isPrimitive = type != null && type.getKind().isPrimitive();
            if (prefix != null && prefix.length() > 0) {
                if (isConst) {
                    name = prefix.toUpperCase(Locale.ENGLISH) + '_' + name;
                } else {
                    name = prefix + name.toUpperCase(Locale.ENGLISH).charAt(0) + name.substring(1);
                }
            }
            int cnt = 1;
            String baseName = name;
            name = CodeStyleUtils.addPrefixSuffix(name, namePrefix, nameSuffix);
            while (isClashing(name, type, locals)) {
                if (isPrimitive) {
                    char c = name.charAt(namePrefix != null ? namePrefix.length() : 0);
                    name = CodeStyleUtils.addPrefixSuffix(Character.toString(++c), namePrefix, nameSuffix);
                    if (c == 'z' || c == 'Z') //NOI18N
                    {
                        isPrimitive = false;
                    }
                } else {
                    name = CodeStyleUtils.addPrefixSuffix(baseName + cnt++, namePrefix, nameSuffix);
                }
            }
            result.add(name);
        }
        return result;
    }

    private static List<String> varNamesForType(TypeMirror type, Types types, Elements elements, String prefix) {
        switch (type.getKind()) {
            case ARRAY:
                TypeElement iterableTE = elements.getTypeElement("java.lang.Iterable"); //NOI18N
                TypeMirror iterable = iterableTE != null ? types.getDeclaredType(iterableTE) : null;
                TypeMirror ct = ((ArrayType) type).getComponentType();
                if (ct.getKind() == TypeKind.ARRAY && iterable != null && types.isSubtype(ct, iterable)) {
                    return varNamesForType(ct, types, elements, prefix);
                }
                List<String> vnct = new ArrayList<>();
                varNamesForType(ct, types, elements, prefix)
                        .forEach(name -> {
                            vnct.add(name.endsWith("s") ? name + "es" : name + "s"); //NOI18N
                        });
                return vnct;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                String str = type.toString().substring(0, 1);
                return prefix != null && !prefix.equals(str)
                        ? Collections.<String>emptyList()
                        : Collections.<String>singletonList(str);
            case TYPEVAR:
                return Collections.<String>singletonList(type.toString().toLowerCase(Locale.ENGLISH));
            case ERROR:
                String tn = ((ErrorType) type).asElement().getSimpleName().toString();
                if (tn.toUpperCase(Locale.ENGLISH).contentEquals(tn)) {
                    return Collections.<String>singletonList(tn.toLowerCase(Locale.ENGLISH));
                }
                StringBuilder sb = new StringBuilder();
                ArrayList<String> al = new ArrayList<>();
                if ("Iterator".equals(tn)) //NOI18N
                {
                    al.add("it"); //NOI18N
                }
                while ((tn = nextName(tn)).length() > 0) {
                    al.add(tn);
                    sb.append(tn.charAt(0));
                }
                if (sb.length() > 0) {
                    String s = sb.toString();
                    if (prefix == null || prefix.length() == 0 || s.startsWith(prefix)) {
                        al.add(s);
                    }
                }
                return al;
            case DECLARED:
                iterableTE = elements.getTypeElement("java.lang.Iterable"); //NOI18N
                iterable = iterableTE != null ? types.getDeclaredType(iterableTE) : null;
                tn = ((DeclaredType) type).asElement().getSimpleName().toString();
                if (tn.toUpperCase(Locale.ENGLISH).contentEquals(tn)) {
                    return Collections.<String>singletonList(tn.toLowerCase(Locale.ENGLISH));
                }
                sb = new StringBuilder();
                al = new ArrayList<>();
                if ("Iterator".equals(tn)) //NOI18N
                {
                    al.add("it"); //NOI18N
                }
                while ((tn = nextName(tn)).length() > 0) {
                    al.add(tn);
                    sb.append(tn.charAt(0));
                }
                if (iterable != null && types.isSubtype(type, iterable)) {
                    List<? extends TypeMirror> tas = ((DeclaredType) type).getTypeArguments();
                    if (tas.size() > 0) {
                        TypeMirror et = tas.get(0);
                        if (et.getKind() == TypeKind.ARRAY || (et.getKind() != TypeKind.WILDCARD && types.isSubtype(et,
                                iterable))) {
                            al.addAll(varNamesForType(et, types, elements, prefix));
                        } else {
                            varNamesForType(et, types, elements, prefix)
                                    .forEach(name -> {
                                        al.add(name.endsWith("s") ? name + "es" : name + "s"); //NOI18N
                                    });
                        }
                    }
                }
                if (sb.length() > 0) {
                    String s = sb.toString();
                    if (prefix == null || prefix.length() == 0 || s.startsWith(prefix)) {
                        al.add(s);
                    }
                }
                return al;
            case WILDCARD:
                TypeMirror bound = ((WildcardType) type).getExtendsBound();
                if (bound == null) {
                    bound = ((WildcardType) type).getSuperBound();
                }
                if (bound != null) {
                    return varNamesForType(bound, types, elements, prefix);
                }
        }
        return Collections.<String>emptyList();
    }

    private static String getConstName(String s) {
        StringBuilder sb = new StringBuilder();
        boolean prevUpper = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!prevUpper) {
                    sb.append('_');
                }
                sb.append(c);
                prevUpper = true;
            } else {
                sb.append(Character.toUpperCase(c));
                prevUpper = false;
            }
        }
        return sb.toString();
    }

    private static String nextName(CharSequence name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                char lc = Character.toLowerCase(c);
                sb.append(lc);
                sb.append(name.subSequence(i + 1, name.length()));
                break;
            }
        }
        return sb.toString();
    }

    private static boolean isClashing(String varName, TypeMirror type, Iterable<? extends Element> locals) {
        if (SourceVersion.isKeyword(varName)) {
            return true;
        }
        if (type != null && type.getKind() == TypeKind.DECLARED && ((DeclaredType) type).asElement().getSimpleName()
                .contentEquals(varName)) {
            return true;
        }
        for (Element e : locals) {
            if ((e.getKind().isField() || e.getKind() == ElementKind.LOCAL_VARIABLE || e.getKind()
                    == ElementKind.RESOURCE_VARIABLE
                    || e.getKind() == ElementKind.PARAMETER || e.getKind() == ElementKind.EXCEPTION_PARAMETER)
                    && varName.contentEquals(e.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWith(String theString, String prefix) {
        if (theString == null || theString.length() == 0 || ConstantDataManager.ANGLED_ERROR.equals(theString)) {
            return false;
        }
        if (prefix == null || prefix.length() == 0) {
            return true;
        }
        return theString.startsWith(prefix);
    }

    public static String createExpression(String expression, ExpressionTree methodCall) {
        int index = expression.indexOf(ConstantDataManager.PARENTHESIZED_ERROR);
        if (index != -1) {
            expression = expression.substring(0, index)
                    + methodCall.toString()
                    + expression.substring(index + ConstantDataManager.PARENTHESIZED_ERROR.length());
        } else {
            index = expression.indexOf(ConstantDataManager.ANGLED_ERROR);
            if (index == -1) {
                return ConstantDataManager.EMPTY_STRING;
            }
            expression = expression.substring(0, index)
                    + methodCall.toString()
                    + expression.substring(index + ConstantDataManager.ANGLED_ERROR.length());
        }
        return expression;
    }
}
