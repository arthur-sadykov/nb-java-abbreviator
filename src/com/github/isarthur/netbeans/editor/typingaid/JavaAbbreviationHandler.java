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

import com.github.isarthur.netbeans.editor.typingaid.spi.AbbreviationHandler;
import com.github.isarthur.netbeans.editor.typingaid.spi.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.FieldAccess;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Keyword;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.LocalElement;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.MethodInvocation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Modifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Name;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.PrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.ui.GenerateCodePanel;
import com.github.isarthur.netbeans.editor.typingaid.ui.PopupUtil;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaAbbreviationHandler implements AbbreviationHandler {

    private final JavaSourceHelper helper;
    private final JTextComponent component;
    private final Document document;

    public JavaAbbreviationHandler(JavaSourceHelper helper) {
        this.helper = helper;
        this.component = helper.getComponent();
        this.document = helper.getDocument();
    }

    @Override
    public List<CodeFragment> process(Abbreviation abbreviation) {
        helper.setAbbreviation(abbreviation);
        if (abbreviation.getContent().contains(".")) { //NOI18N
            if (helper.isCaseLabel()) {
                return Collections.emptyList();
            }
            List<Element> elements = helper.getElementsByAbbreviation();
            if (!elements.isEmpty()) {
                List<MethodInvocation> methodInvocations = Collections.emptyList();
                if (Settings.getSettingForMethodInvocation()) {
                    methodInvocations = helper.collectMethodInvocations(elements);
                }
                List<MethodInvocation> staticMethodInvocations = Collections.emptyList();
                if (Settings.getSettingForStaticMethodInvocation()) {
                    staticMethodInvocations = helper.collectStaticMethodInvocations();
                }
                List<FieldAccess> fieldAccesses = Collections.emptyList();
                if (Settings.getSettingForStaticFieldAccess()) {
                    fieldAccesses = helper.collectFieldAccesses();
                }
                int matchesCount = methodInvocations.size() + staticMethodInvocations.size() + fieldAccesses.size();
                switch (matchesCount) {
                    case 0: {
                        return Collections.emptyList();
                    }
                    case 1: {
                        if (!methodInvocations.isEmpty()) {
                            return helper.insertCodeFragment(methodInvocations.get(0));
                        } else if (!staticMethodInvocations.isEmpty()) {
                            return helper.insertCodeFragment(staticMethodInvocations.get(0));
                        } else {
                            return helper.insertCodeFragment(fieldAccesses.get(0));
                        }
                    }
                    default: {
                        List<CodeFragment> codeFragments = new ArrayList<>();
                        codeFragments.addAll(methodInvocations);
                        codeFragments.addAll(staticMethodInvocations);
                        codeFragments.addAll(fieldAccesses);
                        showPopup(codeFragments);
                        return codeFragments;
                    }
                }
            } else {
                List<MethodInvocation> staticMethodInvocations = Collections.emptyList();
                if (Settings.getSettingForStaticMethodInvocation()) {
                    staticMethodInvocations = helper.collectStaticMethodInvocations();
                }
                List<FieldAccess> fieldAccesses = Collections.emptyList();
                if (Settings.getSettingForStaticFieldAccess()) {
                    fieldAccesses = helper.collectFieldAccesses();
                }
                int matchesCount = staticMethodInvocations.size() + fieldAccesses.size();
                switch (matchesCount) {
                    case 0: {
                        return Collections.emptyList();
                    }
                    case 1: {
                        if (!staticMethodInvocations.isEmpty()) {
                            return helper.insertCodeFragment(staticMethodInvocations.get(0));
                        } else {
                            return helper.insertCodeFragment(fieldAccesses.get(0));
                        }
                    }
                    default: {
                        List<CodeFragment> codeFragments = new ArrayList<>();
                        codeFragments.addAll(staticMethodInvocations);
                        codeFragments.addAll(fieldAccesses);
                        showPopup(codeFragments);
                        return codeFragments;
                    }
                }
            }
        } else {
            if (helper.isMemberSelection()) {
                if (helper.afterThis()) {
                    List<LocalElement> fields = helper.collectFields();
                    int matchesCount = fields.size();
                    switch (matchesCount) {
                        case 0: {
                            return Collections.emptyList();
                        }
                        case 1: {
                            return helper.insertCodeFragment(fields.get(0));
                        }
                        default: {
                            ArrayList<CodeFragment> codeFragments = new ArrayList<>(fields);
                            showPopup(codeFragments);
                            return codeFragments;
                        }
                    }
                } else {
                    List<MethodInvocation> chainedMethodInvocations = Collections.emptyList();
                    if (Settings.getSettingForChainedMethodInvocation()) {
                        chainedMethodInvocations = helper.collectChainedMethodInvocations();
                    }
                    List<FieldAccess> fieldAccesses = Collections.emptyList();
                    if (Settings.getSettingForField()) {
                        fieldAccesses = helper.collectChainedFieldAccesses();
                    }
                    List<FieldAccess> enumConstantAccesses = Collections.emptyList();
                    if (Settings.getSettingForEnumConstant()) {
                        enumConstantAccesses = helper.collectChainedEnumConstantAccesses();
                    }
                    int matchesCount =
                            chainedMethodInvocations.size() + fieldAccesses.size() + enumConstantAccesses.size();
                    switch (matchesCount) {
                        case 0: {
                            return Collections.emptyList();
                        }
                        case 1: {
                            if (!chainedMethodInvocations.isEmpty()) {
                                return helper.insertCodeFragment(chainedMethodInvocations.get(0));
                            } else if (!fieldAccesses.isEmpty()) {
                                return helper.insertCodeFragment(fieldAccesses.get(0));
                            } else {
                                return helper.insertCodeFragment(enumConstantAccesses.get(0));
                            }
                        }
                        default: {
                            ArrayList<CodeFragment> codeFragments = new ArrayList<>();
                            codeFragments.addAll(chainedMethodInvocations);
                            codeFragments.addAll(fieldAccesses);
                            codeFragments.addAll(enumConstantAccesses);
                            showPopup(codeFragments);
                            return codeFragments;
                        }
                    }
                }
            } else {
                if (helper.isFieldOrParameterName()) {
                    List<Name> variableNames = helper.collectVariableNames();
                    int matchesCount = variableNames.size();
                    switch (matchesCount) {
                        case 0: {
                            return Collections.emptyList();
                        }
                        case 1: {
                            return helper.insertName(variableNames.get(0));
                        }
                        default: {
                            List<CodeFragment> codeFragments = new ArrayList<>(variableNames);
                            showPopup(codeFragments);
                            return codeFragments;
                        }
                    }
                } else {
                    if (helper.isCaseLabel()) {
                        if (Settings.getSettingForEnumConstant()) {
                            List<LocalElement> enumConstants = helper.collectEnumConstantsOfSwitchExpressionType();
                            int matchesCount = enumConstants.size();
                            switch (matchesCount) {
                                case 0: {
                                    return Collections.emptyList();
                                }
                                case 1: {
                                    return helper.insertCodeFragment(enumConstants.get(0));
                                }
                                default: {
                                    List<CodeFragment> codeFragments = new ArrayList<>(enumConstants);
                                    showPopup(codeFragments);
                                    return codeFragments;
                                }
                            }
                        }
                        return Collections.emptyList();
                    } else {
                        List<LocalElement> localElements = Collections.emptyList();
                        if (Settings.getSettingForLocalVariable()) {
                            localElements = helper.collectLocalElements();
                        }
                        List<MethodInvocation> localMethodInvocations = Collections.emptyList();
                        if (Settings.getSettingForLocalMethodInvocation()) {
                            localMethodInvocations = helper.collectLocalMethodInvocations();
                        }
                        List<Type> types = Collections.emptyList();
                        if (Settings.getSettingForExternalType()) {
                            types = helper.collectTypes();
                        }
                        List<Keyword> keywords = Collections.emptyList();
                        if (Settings.getSettingForKeyword()) {
                            keywords = helper.collectKeywords();
                        }
                        List<Modifier> modifiers = Collections.emptyList();
                        if (Settings.getSettingForModifier()) {
                            modifiers = helper.collectModifiers();
                        }
                        List<PrimitiveType> primitiveTypes = Collections.emptyList();
                        if (Settings.getSettingForPrimitiveType()) {
                            primitiveTypes = helper.collectPrimitiveTypes();
                        }
                        int matchesCount = localElements.size() + localMethodInvocations.size() + types.size()
                                + keywords.size() + modifiers.size() + primitiveTypes.size();
                        switch (matchesCount) {
                            case 0: {
                                return Collections.emptyList();
                            }
                            case 1: {
                                if (!localElements.isEmpty()) {
                                    return helper.insertCodeFragment(localElements.get(0));
                                } else if (!localMethodInvocations.isEmpty()) {
                                    return helper.insertCodeFragment(localMethodInvocations.get(0));
                                } else if (!types.isEmpty()) {
                                    return helper.insertCodeFragment(types.get(0));
                                } else if (!keywords.isEmpty()) {
                                    return helper.insertKeyword(keywords.get(0));
                                } else if (!modifiers.isEmpty()) {
                                    return helper.insertCodeFragment(modifiers.get(0));
                                } else {
                                    return helper.insertCodeFragment(primitiveTypes.get(0));
                                }
                            }
                            default: {
                                List<CodeFragment> codeFragments = new ArrayList<>();
                                codeFragments.addAll(localElements);
                                codeFragments.addAll(localMethodInvocations);
                                codeFragments.addAll(types);
                                codeFragments.addAll(keywords);
                                codeFragments.addAll(modifiers);
                                codeFragments.addAll(primitiveTypes);
                                Collections.sort(codeFragments, (o1, o2) -> {
                                    return o1.toString().compareTo(o2.toString());
                                });
                                showPopup(codeFragments);
                                return codeFragments;
                            }
                        }
                    }
                }
            }
        }
    }

    private void showPopup(List<CodeFragment> codeFragments) {
        SwingUtilities.invokeLater(() -> {
            try {
                Rectangle caretRectangle = component.modelToView(component.getCaretPosition());
                if (caretRectangle == null) {
                    return;
                }
                Point where = new Point((int) caretRectangle.getX(), (int) (caretRectangle.getY()
                        + caretRectangle.getHeight()));
                SwingUtilities.convertPointToScreen(where, component);
                PopupUtil.showPopup(
                        new GenerateCodePanel(component, codeFragments, helper),
                        (Frame) SwingUtilities.getAncestorOfClass(Frame.class, component),
                        where.getX(),
                        where.getY(),
                        true,
                        caretRectangle.getHeight());
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    public Document getDocument() {
        return document;
    }
}
