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
package com.github.isarthur.netbeans.editor.typingaid.ui;

import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;

final class NetBeansTypingAidPanel extends javax.swing.JPanel {

    private final NetBeansTypingAidOptionsPanelController controller;

    NetBeansTypingAidPanel(NetBeansTypingAidOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        if (!staticMethodInvocationCheckBox.isSelected()) {
            staticMethodInvocationImportedTypesCheckBox.setSelected(false);
        }
        staticMethodInvocationImportedTypesCheckBox.setEnabled(staticMethodInvocationCheckBox.isSelected());
        if (!staticFieldAccessCheckBox.isSelected()) {
            staticFieldAccessImportedTypesCheckBox.setSelected(false);
        }
        staticFieldAccessImportedTypesCheckBox.setEnabled(staticFieldAccessCheckBox.isSelected());
        controller.changed();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        completionForPanel = new javax.swing.JPanel();
        methodInvocationCheckBox = new javax.swing.JCheckBox();
        staticMethodInvocationCheckBox = new javax.swing.JCheckBox();
        chainedMethodInvocationCheckBox = new javax.swing.JCheckBox();
        localMethodInvocationCheckBox = new javax.swing.JCheckBox();
        staticFieldAccessCheckBox = new javax.swing.JCheckBox();
        localVariableCheckBox = new javax.swing.JCheckBox();
        externalTypeCheckBox = new javax.swing.JCheckBox();
        keywordCheckBox = new javax.swing.JCheckBox();
        internalTypeCheckBox = new javax.swing.JCheckBox();
        fieldCheckBox = new javax.swing.JCheckBox();
        parameterCheckBox = new javax.swing.JCheckBox();
        enumConstantCheckBox = new javax.swing.JCheckBox();
        exceptionParameterCheckBox = new javax.swing.JCheckBox();
        resourceVariableCheckBox = new javax.swing.JCheckBox();
        modifierCheckBox = new javax.swing.JCheckBox();
        primitiveTypeCheckBox = new javax.swing.JCheckBox();
        importedTypeCheckBox = new javax.swing.JCheckBox();
        staticFieldAccessImportedTypesCheckBox = new javax.swing.JCheckBox();
        staticMethodInvocationImportedTypesCheckBox = new javax.swing.JCheckBox();
        chainedFieldAccessCheckBox = new javax.swing.JCheckBox();
        chainedEnumConstantCheckBox = new javax.swing.JCheckBox();
        samePackageTypeCheckBox = new javax.swing.JCheckBox();
        literalCheckBox = new javax.swing.JCheckBox();

        completionForPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.completionForPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(methodInvocationCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.methodInvocationCheckBox.text")); // NOI18N
        methodInvocationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodInvocationCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(staticMethodInvocationCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.staticMethodInvocationCheckBox.text")); // NOI18N
        staticMethodInvocationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticMethodInvocationCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chainedMethodInvocationCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.chainedMethodInvocationCheckBox.text")); // NOI18N
        chainedMethodInvocationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chainedMethodInvocationCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(localMethodInvocationCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.localMethodInvocationCheckBox.text")); // NOI18N
        localMethodInvocationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localMethodInvocationCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(staticFieldAccessCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.staticFieldAccessCheckBox.text")); // NOI18N
        staticFieldAccessCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticFieldAccessCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(localVariableCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.localVariableCheckBox.text")); // NOI18N
        localVariableCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localVariableCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(externalTypeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.externalTypeCheckBox.text")); // NOI18N
        externalTypeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalTypeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(keywordCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.keywordCheckBox.text")); // NOI18N
        keywordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(internalTypeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.internalTypeCheckBox.text")); // NOI18N
        internalTypeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internalTypeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(fieldCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.fieldCheckBox.text")); // NOI18N
        fieldCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(parameterCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.parameterCheckBox.text")); // NOI18N
        parameterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameterCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(enumConstantCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.enumConstantCheckBox.text")); // NOI18N
        enumConstantCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enumConstantCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(exceptionParameterCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.exceptionParameterCheckBox.text")); // NOI18N
        exceptionParameterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exceptionParameterCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(resourceVariableCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.resourceVariableCheckBox.text")); // NOI18N
        resourceVariableCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourceVariableCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(modifierCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.modifierCheckBox.text")); // NOI18N
        modifierCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifierCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(primitiveTypeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.primitiveTypeCheckBox.text")); // NOI18N
        primitiveTypeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                primitiveTypeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(importedTypeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.importedTypeCheckBox.text")); // NOI18N
        importedTypeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importedTypeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(staticFieldAccessImportedTypesCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.staticFieldAccessImportedTypesCheckBox.text")); // NOI18N
        staticFieldAccessImportedTypesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticFieldAccessImportedTypesCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(staticMethodInvocationImportedTypesCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.staticMethodInvocationImportedTypesCheckBox.text")); // NOI18N
        staticMethodInvocationImportedTypesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticMethodInvocationImportedTypesCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chainedFieldAccessCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.chainedFieldAccessCheckBox.text")); // NOI18N
        chainedFieldAccessCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chainedFieldAccessCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chainedEnumConstantCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.chainedEnumConstantCheckBox.text")); // NOI18N
        chainedEnumConstantCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chainedEnumConstantCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(samePackageTypeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.samePackageTypeCheckBox.text")); // NOI18N
        samePackageTypeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                samePackageTypeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(literalCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.literalCheckBox.text")); // NOI18N
        literalCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                literalCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout completionForPanelLayout = new javax.swing.GroupLayout(completionForPanel);
        completionForPanel.setLayout(completionForPanelLayout);
        completionForPanelLayout.setHorizontalGroup(
            completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(completionForPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(completionForPanelLayout.createSequentialGroup()
                        .addComponent(literalCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(completionForPanelLayout.createSequentialGroup()
                        .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(localMethodInvocationCheckBox)
                            .addComponent(staticFieldAccessCheckBox)
                            .addComponent(localVariableCheckBox)
                            .addComponent(externalTypeCheckBox)
                            .addComponent(internalTypeCheckBox)
                            .addComponent(fieldCheckBox)
                            .addComponent(parameterCheckBox)
                            .addComponent(enumConstantCheckBox)
                            .addComponent(exceptionParameterCheckBox)
                            .addComponent(resourceVariableCheckBox)
                            .addComponent(modifierCheckBox)
                            .addComponent(primitiveTypeCheckBox)
                            .addGroup(completionForPanelLayout.createSequentialGroup()
                                .addGap(177, 177, 177)
                                .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(staticMethodInvocationImportedTypesCheckBox)
                                    .addComponent(staticFieldAccessImportedTypesCheckBox)))
                            .addComponent(staticMethodInvocationCheckBox)
                            .addComponent(methodInvocationCheckBox)
                            .addComponent(chainedMethodInvocationCheckBox)
                            .addComponent(chainedFieldAccessCheckBox)
                            .addComponent(chainedEnumConstantCheckBox)
                            .addComponent(keywordCheckBox)
                            .addComponent(importedTypeCheckBox)
                            .addComponent(samePackageTypeCheckBox))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        completionForPanelLayout.setVerticalGroup(
            completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, completionForPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(staticMethodInvocationCheckBox)
                    .addComponent(staticMethodInvocationImportedTypesCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(staticFieldAccessCheckBox)
                    .addComponent(staticFieldAccessImportedTypesCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(methodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chainedMethodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chainedFieldAccessCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chainedEnumConstantCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localMethodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localVariableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enumConstantCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exceptionParameterCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resourceVariableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(internalTypeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(externalTypeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importedTypeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(samePackageTypeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(keywordCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(literalCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modifierCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(primitiveTypeCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completionForPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completionForPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void methodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_methodInvocationCheckBoxActionPerformed

    private void staticMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticMethodInvocationCheckBoxActionPerformed
        if (!staticMethodInvocationCheckBox.isSelected()) {
            staticMethodInvocationImportedTypesCheckBox.setSelected(false);
        }
        staticMethodInvocationImportedTypesCheckBox.setEnabled(staticMethodInvocationCheckBox.isSelected());
        controller.changed();
    }//GEN-LAST:event_staticMethodInvocationCheckBoxActionPerformed

    private void chainedMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainedMethodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_chainedMethodInvocationCheckBoxActionPerformed

    private void localMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localMethodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_localMethodInvocationCheckBoxActionPerformed

    private void staticFieldAccessCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticFieldAccessCheckBoxActionPerformed
        if (!staticFieldAccessCheckBox.isSelected()) {
            staticFieldAccessImportedTypesCheckBox.setSelected(false);
        }
        staticFieldAccessImportedTypesCheckBox.setEnabled(staticFieldAccessCheckBox.isSelected());
        controller.changed();
    }//GEN-LAST:event_staticFieldAccessCheckBoxActionPerformed

    private void localVariableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localVariableCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_localVariableCheckBoxActionPerformed

    private void externalTypeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalTypeCheckBoxActionPerformed
        if (externalTypeCheckBox.isSelected()) {
            importedTypeCheckBox.setSelected(false);
            samePackageTypeCheckBox.setSelected(false);
        }
        controller.changed();
    }//GEN-LAST:event_externalTypeCheckBoxActionPerformed

    private void keywordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_keywordCheckBoxActionPerformed

    private void fieldCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_fieldCheckBoxActionPerformed

    private void parameterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parameterCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_parameterCheckBoxActionPerformed

    private void enumConstantCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enumConstantCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_enumConstantCheckBoxActionPerformed

    private void exceptionParameterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exceptionParameterCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_exceptionParameterCheckBoxActionPerformed

    private void resourceVariableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourceVariableCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_resourceVariableCheckBoxActionPerformed

    private void internalTypeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internalTypeCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_internalTypeCheckBoxActionPerformed

    private void modifierCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifierCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_modifierCheckBoxActionPerformed

    private void primitiveTypeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_primitiveTypeCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_primitiveTypeCheckBoxActionPerformed

    private void importedTypeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importedTypeCheckBoxActionPerformed
        if (importedTypeCheckBox.isSelected()) {
            externalTypeCheckBox.setSelected(false);
        }
        controller.changed();
    }//GEN-LAST:event_importedTypeCheckBoxActionPerformed

    private void staticMethodInvocationImportedTypesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticMethodInvocationImportedTypesCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_staticMethodInvocationImportedTypesCheckBoxActionPerformed

    private void staticFieldAccessImportedTypesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticFieldAccessImportedTypesCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_staticFieldAccessImportedTypesCheckBoxActionPerformed

    private void chainedFieldAccessCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainedFieldAccessCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_chainedFieldAccessCheckBoxActionPerformed

    private void chainedEnumConstantCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainedEnumConstantCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_chainedEnumConstantCheckBoxActionPerformed

    private void samePackageTypeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_samePackageTypeCheckBoxActionPerformed
        if (samePackageTypeCheckBox.isSelected()) {
            externalTypeCheckBox.setSelected(false);
        }
        controller.changed();
    }//GEN-LAST:event_samePackageTypeCheckBoxActionPerformed

    private void literalCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_literalCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_literalCheckBoxActionPerformed

    void load() {
        methodInvocationCheckBox.setSelected(Preferences.getMethodInvocationFlag());
        staticMethodInvocationCheckBox.setSelected(Preferences.getStaticMethodInvocationFlag());
        chainedMethodInvocationCheckBox.setSelected(Preferences.getChainedMethodInvocationFlag());
        chainedFieldAccessCheckBox.setSelected(Preferences.getChainedFieldAccessFlag());
        chainedEnumConstantCheckBox.setSelected(Preferences.getChainedEnumConstantAccessFlag());
        localMethodInvocationCheckBox.setSelected(Preferences.getLocalMethodInvocationFlag());
        staticFieldAccessCheckBox.setSelected(Preferences.getStaticFieldAccessFlag());
        localVariableCheckBox.setSelected(Preferences.getLocalVariableFlag());
        fieldCheckBox.setSelected(Preferences.getFieldFlag());
        parameterCheckBox.setSelected(Preferences.getParameterFlag());
        enumConstantCheckBox.setSelected(Preferences.getEnumConstantFlag());
        exceptionParameterCheckBox.setSelected(Preferences.getExceptionParameterFlag());
        resourceVariableCheckBox.setSelected(Preferences.getResourceVariableFlag());
        internalTypeCheckBox.setSelected(Preferences.getInternalTypeFlag());
        externalTypeCheckBox.setSelected(Preferences.getExternalTypeFlag());
        importedTypeCheckBox.setSelected(Preferences.getImportedTypeFlag());
        keywordCheckBox.setSelected(Preferences.getKeywordFlag());
        literalCheckBox.setSelected(Preferences.getLiteralFlag());
        modifierCheckBox.setSelected(Preferences.getModifierFlag());
        primitiveTypeCheckBox.setSelected(Preferences.getPrimitiveTypeFlag());
        samePackageTypeCheckBox.setSelected(Preferences.getSamePackageTypeFlag());
        staticMethodInvocationImportedTypesCheckBox.setSelected(Preferences.getStaticMethodInvocationImportedTypesFlag());
        staticFieldAccessImportedTypesCheckBox.setSelected(Preferences.getStaticFieldAccessImportedTypesFlag());
    }

    void store() {
        Preferences.setMethodInvocationFlag(methodInvocationCheckBox.isSelected());
        Preferences.setStaticMethodInvocationFlag(staticMethodInvocationCheckBox.isSelected());
        Preferences.setChainedMethodInvocationFlag(chainedMethodInvocationCheckBox.isSelected());
        Preferences.setChainedFieldAccessFlag(chainedFieldAccessCheckBox.isSelected());
        Preferences.setChainedEnumConstantAccessFlag(chainedEnumConstantCheckBox.isSelected());
        Preferences.setLocalMethodInvocationFlag(localMethodInvocationCheckBox.isSelected());
        Preferences.setStaticFieldAccessFlag(staticFieldAccessCheckBox.isSelected());
        Preferences.setLocalVariableFlag(localVariableCheckBox.isSelected());
        Preferences.setFieldFlag(fieldCheckBox.isSelected());
        Preferences.setParameterFlag(parameterCheckBox.isSelected());
        Preferences.setEnumConstantFlag(enumConstantCheckBox.isSelected());
        Preferences.setExceptionParameterFlag(exceptionParameterCheckBox.isSelected());
        Preferences.setResourceVariableFlag(resourceVariableCheckBox.isSelected());
        Preferences.setInternalTypeFlag(internalTypeCheckBox.isSelected());
        Preferences.setExternalTypeFlag(externalTypeCheckBox.isSelected());
        Preferences.setImportedTypeFlag(importedTypeCheckBox.isSelected());
        Preferences.setKeywordFlag(keywordCheckBox.isSelected());
        Preferences.setLiteralFlag(literalCheckBox.isSelected());
        Preferences.setModifierFlag(modifierCheckBox.isSelected());
        Preferences.setPrimitiveTypeFlag(primitiveTypeCheckBox.isSelected());
        Preferences.setSamePackageTypeFlag(samePackageTypeCheckBox.isSelected());
        Preferences.setStaticMethodInvocationImportedTypesFlag(staticMethodInvocationImportedTypesCheckBox.isSelected());
        Preferences.setStaticFieldAccessImportedTypesFlag(staticFieldAccessImportedTypesCheckBox.isSelected());
    }

    boolean valid() {
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chainedEnumConstantCheckBox;
    private javax.swing.JCheckBox chainedFieldAccessCheckBox;
    private javax.swing.JCheckBox chainedMethodInvocationCheckBox;
    private javax.swing.JPanel completionForPanel;
    private javax.swing.JCheckBox enumConstantCheckBox;
    private javax.swing.JCheckBox exceptionParameterCheckBox;
    private javax.swing.JCheckBox externalTypeCheckBox;
    private javax.swing.JCheckBox fieldCheckBox;
    private javax.swing.JCheckBox importedTypeCheckBox;
    private javax.swing.JCheckBox internalTypeCheckBox;
    private javax.swing.JCheckBox keywordCheckBox;
    private javax.swing.JCheckBox literalCheckBox;
    private javax.swing.JCheckBox localMethodInvocationCheckBox;
    private javax.swing.JCheckBox localVariableCheckBox;
    private javax.swing.JCheckBox methodInvocationCheckBox;
    private javax.swing.JCheckBox modifierCheckBox;
    private javax.swing.JCheckBox parameterCheckBox;
    private javax.swing.JCheckBox primitiveTypeCheckBox;
    private javax.swing.JCheckBox resourceVariableCheckBox;
    private javax.swing.JCheckBox samePackageTypeCheckBox;
    private javax.swing.JCheckBox staticFieldAccessCheckBox;
    private javax.swing.JCheckBox staticFieldAccessImportedTypesCheckBox;
    private javax.swing.JCheckBox staticMethodInvocationCheckBox;
    private javax.swing.JCheckBox staticMethodInvocationImportedTypesCheckBox;
    // End of variables declaration//GEN-END:variables
}
