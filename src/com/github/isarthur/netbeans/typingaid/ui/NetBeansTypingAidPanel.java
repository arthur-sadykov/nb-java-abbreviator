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
package com.github.isarthur.netbeans.typingaid.ui;

import com.github.isarthur.netbeans.typingaid.settings.Settings;

final class NetBeansTypingAidPanel extends javax.swing.JPanel {

    private final NetBeansTypingAidOptionsPanelController controller;

    NetBeansTypingAidPanel(NetBeansTypingAidOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
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
        typeCheckBox = new javax.swing.JCheckBox();
        keywordCheckBox = new javax.swing.JCheckBox();

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

        org.openide.awt.Mnemonics.setLocalizedText(typeCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.typeCheckBox.text")); // NOI18N
        typeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(keywordCheckBox, org.openide.util.NbBundle.getMessage(NetBeansTypingAidPanel.class, "NetBeansTypingAidPanel.keywordCheckBox.text")); // NOI18N
        keywordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout completionForPanelLayout = new javax.swing.GroupLayout(completionForPanel);
        completionForPanel.setLayout(completionForPanelLayout);
        completionForPanelLayout.setHorizontalGroup(
            completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(completionForPanelLayout.createSequentialGroup()
                .addGroup(completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(methodInvocationCheckBox)
                    .addComponent(staticMethodInvocationCheckBox)
                    .addComponent(chainedMethodInvocationCheckBox)
                    .addComponent(localMethodInvocationCheckBox)
                    .addComponent(staticFieldAccessCheckBox)
                    .addComponent(localVariableCheckBox)
                    .addComponent(typeCheckBox)
                    .addComponent(keywordCheckBox))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        completionForPanelLayout.setVerticalGroup(
            completionForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(completionForPanelLayout.createSequentialGroup()
                .addComponent(methodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(staticMethodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chainedMethodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localMethodInvocationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(staticFieldAccessCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localVariableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(keywordCheckBox)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(completionForPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void methodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_methodInvocationCheckBoxActionPerformed

    private void staticMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticMethodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_staticMethodInvocationCheckBoxActionPerformed

    private void chainedMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainedMethodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_chainedMethodInvocationCheckBoxActionPerformed

    private void localMethodInvocationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localMethodInvocationCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_localMethodInvocationCheckBoxActionPerformed

    private void staticFieldAccessCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticFieldAccessCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_staticFieldAccessCheckBoxActionPerformed

    private void localVariableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localVariableCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_localVariableCheckBoxActionPerformed

    private void typeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_typeCheckBoxActionPerformed

    private void keywordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_keywordCheckBoxActionPerformed

    void load() {
        methodInvocationCheckBox.setSelected(Settings.getSettingForMethodInvocation());
        staticMethodInvocationCheckBox.setSelected(Settings.getSettingForStaticMethodInvocation());
        chainedMethodInvocationCheckBox.setSelected(Settings.getSettingForChainedMethodInvocation());
        localMethodInvocationCheckBox.setSelected(Settings.getSettingForLocalMethodInvocation());
        staticFieldAccessCheckBox.setSelected(Settings.getSettingForStaticFieldAccess());
        localVariableCheckBox.setSelected(Settings.getSettingForLocalVariable());
        typeCheckBox.setSelected(Settings.getSettingForType());
        keywordCheckBox.setSelected(Settings.getSettingForKeyword());
    }

    void store() {
        Settings.setSettingForMethodInvocation(methodInvocationCheckBox.isSelected());
        Settings.setSettingForStaticMethodInvocation(staticMethodInvocationCheckBox.isSelected());
        Settings.setSettingForChainedMethodInvocation(chainedMethodInvocationCheckBox.isSelected());
        Settings.setSettingForLocalMethodInvocation(localMethodInvocationCheckBox.isSelected());
        Settings.setSettingForStaticFieldAccess(staticFieldAccessCheckBox.isSelected());
        Settings.setSettingForLocalVariable(localVariableCheckBox.isSelected());
        Settings.setSettingForType(typeCheckBox.isSelected());
        Settings.setSettingForKeyword(keywordCheckBox.isSelected());
    }

    boolean valid() {
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chainedMethodInvocationCheckBox;
    private javax.swing.JPanel completionForPanel;
    private javax.swing.JCheckBox keywordCheckBox;
    private javax.swing.JCheckBox localMethodInvocationCheckBox;
    private javax.swing.JCheckBox localVariableCheckBox;
    private javax.swing.JCheckBox methodInvocationCheckBox;
    private javax.swing.JCheckBox staticFieldAccessCheckBox;
    private javax.swing.JCheckBox staticMethodInvocationCheckBox;
    private javax.swing.JCheckBox typeCheckBox;
    // End of variables declaration//GEN-END:variables
}
