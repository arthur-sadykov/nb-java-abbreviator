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

import com.github.isarthur.netbeans.editor.typingaid.collector.impl.ChainedMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.Collector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.LocalVariableCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.impl.MethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.ChainedLinker;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.CompoundLinker;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.api.Linker;
import com.github.isarthur.netbeans.editor.typingaid.collector.linker.impl.SimpleLinker;
import com.github.isarthur.netbeans.editor.typingaid.api.AbbreviationHandler;
import com.github.isarthur.netbeans.editor.typingaid.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.ui.GenerateCodePanel;
import com.github.isarthur.netbeans.editor.typingaid.ui.PopupUtil;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                Request request = new Request(codeFragments, helper, controller);
                if (abbreviation.getContent().contains(".")) { //NOI18N
                    Collector collector = new MethodInvocationCollector();
                    Linker linker = new CompoundLinker(collector);
                    collector = linker.link();
                    if (collector != null) {
                        collector.collect(request);
                    }
                } else {
                    if (helper.isMemberSelection(controller)) {
                        Collector collector = new ChainedMethodInvocationCollector();
                        Linker linker = new ChainedLinker(collector);
                        collector = linker.link();
                        if (collector != null) {
                            collector.collect(request);
                        }
                    } else {
                        Collector collector = new LocalVariableCollector();
                        Linker linker = new SimpleLinker(collector);
                        collector = linker.link();
                        if (collector != null) {
                            collector.collect(request);
                        }
                    }
                }
                int matchesCount = codeFragments.size();
                switch (matchesCount) {
                    case 0:
                        break;
                    case 1:
                        helper.insertCodeFragment(codeFragments.get(0));
                        break;
                    default:
                        codeFragments.sort((fragment1, fragment2) ->
                                fragment1.toString().compareTo(fragment2.toString()));
                        showPopup(codeFragments);
                }
            }, true);
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
