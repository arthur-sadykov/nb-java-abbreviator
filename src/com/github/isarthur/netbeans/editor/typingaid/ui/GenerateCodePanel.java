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

import com.github.isarthur.netbeans.editor.typingaid.JavaSourceHelper;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Utilities;

/**
 *
 * @author Arthur Sadykov
 */
public class GenerateCodePanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 9080172839928978517L;
    private final JTextComponent component;
    private final JavaSourceHelper helper;

    public GenerateCodePanel(JTextComponent component, List<? extends CodeFragment> memberSelections,
            JavaSourceHelper helper) {
        this.component = component;
        this.helper = helper;
        initComponents();
        setFocusable(false);
        setBackground(codeFragmentsList.getBackground());
        scrollPane.setBackground(codeFragmentsList.getBackground());
        codeFragmentsList.setModel(createModel(memberSelections));
        codeFragmentsList.setSelectedIndex(0);
        codeFragmentsList.setVisibleRowCount(memberSelections.size() > 16 ? 16 : memberSelections.size());
        codeFragmentsList.setCellRenderer(new Renderer(codeFragmentsList));
        codeFragmentsList.grabFocus();
        codeFragmentsList.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                PopupUtil.hidePopup();
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        codeFragmentsList = new javax.swing.JList<>();
        generateCodeLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(64, 64, 64)));
        setLayout(new java.awt.BorderLayout());

        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 4, 4));

        codeFragmentsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listMouseReleased(evt);
            }
        });
        codeFragmentsList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                listMouseMoved(evt);
            }
        });
        codeFragmentsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(codeFragmentsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        generateCodeLabel.setText(org.openide.util.NbBundle.getMessage(GenerateCodePanel.class, "LBL_generate_code")); // NOI18N
        generateCodeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        generateCodeLabel.setOpaque(true);
        add(generateCodeLabel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void listMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseReleased
        invokeSelected();
    }//GEN-LAST:event_listMouseReleased

    private void listMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseMoved
        int idx = codeFragmentsList.locationToIndex(evt.getPoint());
        if (idx != codeFragmentsList.getSelectedIndex()) {
            codeFragmentsList.setSelectedIndex(idx);
        }
    }//GEN-LAST:event_listMouseMoved

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(evt);
        switch (keyStroke.getKeyCode()) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                invokeSelected();
                break;
            case KeyEvent.VK_DOWN: {
                int size = codeFragmentsList.getModel().getSize();
                if (size > 0) {
                    int idx = (codeFragmentsList.getSelectedIndex() + 1) % size;
                    if (idx == size) {
                        idx = 0;
                    }
                    codeFragmentsList.setSelectedIndex(idx);
                    codeFragmentsList.ensureIndexIsVisible(idx);
                    evt.consume();
                }
                break;
            }
            case KeyEvent.VK_UP: {
                int size = codeFragmentsList.getModel().getSize();
                if (size > 0) {
                    int idx = (codeFragmentsList.getSelectedIndex() - 1 + size) % size;
                    codeFragmentsList.setSelectedIndex(idx);
                    codeFragmentsList.ensureIndexIsVisible(idx);
                    evt.consume();
                }
                break;
            }
            default:
                break;
        }
    }//GEN-LAST:event_listKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JList<CodeFragment> codeFragmentsList;
    public javax.swing.JLabel generateCodeLabel;
    public javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    private DefaultListModel<CodeFragment> createModel(List<? extends CodeFragment> memberSelections) {
        DefaultListModel<CodeFragment> model = new DefaultListModel<>();
        memberSelections.forEach(memberSelection -> {
            model.addElement(memberSelection);
        });
        return model;
    }

    private void invokeSelected() {
        PopupUtil.hidePopup();
        if (Utilities.isMac()) {
            component.requestFocus();
        }
        CodeFragment codeFragment = codeFragmentsList.getSelectedValue();
        helper.insertCodeFragment(codeFragment);
    }

    private static class Renderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -9013237139592493066L;
        private static final int DARKER_COLOR_COMPONENT = 5;
        private final Color foregroundColor;
        private final Color backgroundColor;
        private final Color backgroundColorDarker;
        private final Color backgroundSelectionColor;
        private final Color foregroundSelectionColor;

        Renderer(JList<CodeFragment> list) {
            setFont(list.getFont());
            foregroundColor = list.getForeground();
            backgroundColor = list.getBackground();
            backgroundColorDarker = new Color(Math.abs(backgroundColor.getRed() - DARKER_COLOR_COMPONENT),
                    Math.abs(backgroundColor.getGreen() - DARKER_COLOR_COMPONENT),
                    Math.abs(backgroundColor.getBlue() - DARKER_COLOR_COMPONENT));
            backgroundSelectionColor = list.getSelectionBackground();
            foregroundSelectionColor = list.getSelectionForeground();
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean hasFocus) {
            if (isSelected) {
                setForeground(foregroundSelectionColor);
                setBackground(backgroundSelectionColor);
            } else {
                setForeground(foregroundColor);
                setBackground(index % 2 == 0 ? backgroundColor : backgroundColorDarker);
            }
            if (value != null) {
                setText(value instanceof CodeGenerator ? ((CodeGenerator) value).getDisplayName() : value.toString());
            }
            return this;
        }
    }
}
