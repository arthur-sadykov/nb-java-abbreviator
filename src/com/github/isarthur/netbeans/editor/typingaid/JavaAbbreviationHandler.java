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
import com.github.isarthur.netbeans.editor.typingaid.codefragment.PrimitiveType;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.Type;
import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.ui.GenerateCodePanel;
import com.github.isarthur.netbeans.editor.typingaid.ui.PopupUtil;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.JavaSource;
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
        List<CodeFragment> codeFragments = new ArrayList<>();
        helper.setAbbreviation(abbreviation);
        JavaSource javaSource = helper.getJavaSourceForDocument(document);
        try {
            javaSource.runUserActionTask(controller -> {
                helper.moveStateToParsedPhase(controller);
                if (abbreviation.getContent().contains(".")) { //NOI18N
                    List<Element> elements = helper.getElementsByAbbreviation(controller);
                    if (!elements.isEmpty()) {
                        List<MethodInvocation> methodInvocations = Collections.emptyList();
                        if (Settings.getSettingForMethodInvocation()) {
                            methodInvocations = helper.collectMethodInvocations(elements, controller);
                        }
                        List<MethodInvocation> staticMethodInvocations = Collections.emptyList();
                        if (Settings.getSettingForStaticMethodInvocation()) {
                            staticMethodInvocations = helper.collectStaticMethodInvocations(controller);
                        }
                        List<FieldAccess> fieldAccesses = Collections.emptyList();
                        if (Settings.getSettingForStaticFieldAccess()) {
                            fieldAccesses = helper.collectFieldAccesses(controller);
                        }
                        int matchesCount = methodInvocations.size() + staticMethodInvocations.size()
                                + fieldAccesses.size();
                        switch (matchesCount) {
                            case 0: {
                                break;
                            }
                            case 1:
                                if (!methodInvocations.isEmpty()) {
                                    codeFragments.addAll(helper.insertCodeFragment(methodInvocations.get(0)));
                                } else if (!staticMethodInvocations.isEmpty()) {
                                    codeFragments.addAll(helper.insertCodeFragment(staticMethodInvocations.get(0)));
                                } else {
                                    codeFragments.addAll(helper.insertCodeFragment(fieldAccesses.get(0)));
                                }
                                break;
                            default:
                                codeFragments.addAll(methodInvocations);
                                codeFragments.addAll(staticMethodInvocations);
                                codeFragments.addAll(fieldAccesses);
                                showPopup(codeFragments);
                        }
                    } else {
                        List<MethodInvocation> staticMethodInvocations = Collections.emptyList();
                        if (Settings.getSettingForStaticMethodInvocation()) {
                            staticMethodInvocations = helper.collectStaticMethodInvocations(controller);
                        }
                        List<FieldAccess> fieldAccesses = Collections.emptyList();
                        if (Settings.getSettingForStaticFieldAccess()) {
                            fieldAccesses = helper.collectFieldAccesses(controller);
                        }
                        int matchesCount = staticMethodInvocations.size() + fieldAccesses.size();
                        switch (matchesCount) {
                            case 0:
                                break;
                            case 1:
                                if (!staticMethodInvocations.isEmpty()) {
                                    codeFragments.addAll(helper.insertCodeFragment(staticMethodInvocations.get(0)));
                                } else {
                                    codeFragments.addAll(helper.insertCodeFragment(fieldAccesses.get(0)));
                                }
                                break;
                            default:
                                codeFragments.addAll(staticMethodInvocations);
                                codeFragments.addAll(fieldAccesses);
                                showPopup(codeFragments);
                        }
                    }
                } else {
                    if (helper.isMemberSelection(controller)) {
                        if (helper.afterThis(controller)) {
                            List<LocalElement> fields = helper.collectFields(controller);
                            int matchesCount = fields.size();
                            switch (matchesCount) {
                                case 0:
                                    break;
                                case 1:
                                    codeFragments.addAll(helper.insertCodeFragment(fields.get(0)));
                                    break;
                                default:
                                    codeFragments.addAll(fields);
                                    showPopup(codeFragments);
                            }
                        } else {
                            List<MethodInvocation> chainedMethodInvocations = Collections.emptyList();
                            if (Settings.getSettingForChainedMethodInvocation()) {
                                chainedMethodInvocations = helper.collectChainedMethodInvocations(controller);
                            }
                            List<FieldAccess> fieldAccesses = Collections.emptyList();
                            if (Settings.getSettingForField()) {
                                fieldAccesses = helper.collectChainedFieldAccesses(controller);
                            }
                            List<FieldAccess> enumConstantAccesses = Collections.emptyList();
                            if (Settings.getSettingForEnumConstant()) {
                                enumConstantAccesses = helper.collectChainedEnumConstantAccesses(controller);
                            }
                            int matchesCount =
                                    chainedMethodInvocations.size() + fieldAccesses.size() + enumConstantAccesses.size();
                            switch (matchesCount) {
                                case 0:
                                    break;
                                case 1:
                                    if (!chainedMethodInvocations.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(chainedMethodInvocations.get(0)));
                                    } else if (!fieldAccesses.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(fieldAccesses.get(0)));
                                    } else {
                                        codeFragments.addAll(helper.insertCodeFragment(enumConstantAccesses.get(0)));
                                    }
                                    break;
                                default:
                                    codeFragments.addAll(chainedMethodInvocations);
                                    codeFragments.addAll(fieldAccesses);
                                    codeFragments.addAll(enumConstantAccesses);
                                    showPopup(codeFragments);
                            }
                        }
                    } else {
                        if (helper.isCaseLabel(controller)) {
                            if (Settings.getSettingForEnumConstant()) {
                                List<LocalElement> enumConstants =
                                        helper.collectEnumConstantsOfSwitchExpressionType(controller);
                                int matchesCount = enumConstants.size();
                                switch (matchesCount) {
                                    case 0:
                                        break;
                                    case 1:
                                        codeFragments.addAll(helper.insertCodeFragment(enumConstants.get(0)));
                                        break;
                                    default:
                                        codeFragments.addAll(enumConstants);
                                        showPopup(codeFragments);
                                }
                            }
                        } else {
                            List<LocalElement> localElements = Collections.emptyList();
                            if (Settings.getSettingForLocalVariable()) {
                                localElements = helper.collectLocalElements(controller);
                            }
                            List<Type> localTypes = Collections.emptyList();
                            if (Settings.getSettingForInternalType()) {
                                localTypes = helper.collectLocalTypes(controller);
                            }
                            List<MethodInvocation> localMethodInvocations = Collections.emptyList();
                            if (Settings.getSettingForLocalMethodInvocation()) {
                                localMethodInvocations = helper.collectLocalMethodInvocations(controller);
                            }
                            List<Type> types = Collections.emptyList();
                            if (Settings.getSettingForExternalType()) {
                                types = helper.collectTypes(controller);
                            }
                            List<Type> importedTypes = Collections.emptyList();
                            if (Settings.getSettingForImportedType()) {
                                types = helper.collectImportedTypes(controller);
                            }
                            List<Keyword> keywords = Collections.emptyList();
                            if (Settings.getSettingForKeyword()) {
                                keywords = helper.collectKeywords(controller);
                            }
                            List<Modifier> modifiers = Collections.emptyList();
                            if (Settings.getSettingForModifier()) {
                                modifiers = helper.collectModifiers(controller);
                            }
                            List<PrimitiveType> primitiveTypes = Collections.emptyList();
                            if (Settings.getSettingForPrimitiveType()) {
                                primitiveTypes = helper.collectPrimitiveTypes(controller);
                            }
                            int matchesCount = localElements.size() + localTypes.size() + localMethodInvocations.size()
                                    + types.size() + importedTypes.size() + keywords.size() + modifiers.size()
                                    + primitiveTypes.size();
                            switch (matchesCount) {
                                case 0:
                                    break;
                                case 1:
                                    if (!localElements.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(localElements.get(0)));
                                    } else if (!localTypes.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(localTypes.get(0)));
                                    } else if (!localMethodInvocations.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(localMethodInvocations.get(0)));
                                    } else if (!types.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(types.get(0)));
                                    } else if (!importedTypes.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(importedTypes.get(0)));
                                    } else if (!keywords.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(keywords.get(0)));
                                    } else if (!modifiers.isEmpty()) {
                                        codeFragments.addAll(helper.insertCodeFragment(modifiers.get(0)));
                                    } else {
                                        codeFragments.addAll(helper.insertCodeFragment(primitiveTypes.get(0)));
                                    }
                                    break;
                                default:
                                    codeFragments.addAll(localElements);
                                    codeFragments.addAll(localTypes);
                                    codeFragments.addAll(localMethodInvocations);
                                    codeFragments.addAll(types);
                                    codeFragments.addAll(importedTypes);
                                    codeFragments.addAll(keywords);
                                    codeFragments.addAll(modifiers);
                                    codeFragments.addAll(primitiveTypes);
                                    Collections.sort(codeFragments, (o1, o2) -> {
                                        return o1.toString().compareTo(o2.toString());
                                    });
                                    showPopup(codeFragments);
                            }
                        }
                    }
                }
            },
                    true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.unmodifiableList(codeFragments);
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
