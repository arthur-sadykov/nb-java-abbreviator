/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.beans.PropertyChangeListener;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author Arthur Sadykov
 */
public class Installer extends ModuleInstall {

    private static final long serialVersionUID = 1L;
    private final PropertyChangeListener editorsTracker = event -> {
        if (EditorRegistry.FOCUS_GAINED_PROPERTY.equals(event.getPropertyName())
                || event.getPropertyName() == null) {
            Object newValue = event.getNewValue();
            if (!(newValue instanceof JEditorPane)) {
                return;
            }
            JEditorPane editor = (JEditorPane) newValue;
            String contentType = editor.getContentType();
            if (!contentType.equals("text/x-java")) {
                return;
            }
            AbbrevDetection.get(editor);
        } else if (EditorRegistry.FOCUS_LOST_PROPERTY.equals(event.getPropertyName())) {
            Object oldValue = event.getOldValue();
            if (!(oldValue instanceof JEditorPane)) {
                return;
            }
            JEditorPane editor = (JEditorPane) oldValue;
            String contentType = editor.getContentType();
            if (!contentType.equals("text/x-java")) {
                return;
            }
            AbbrevDetection.remove(editor);
        }
    };

    @Override
    public void restored() {
        EditorRegistry.addPropertyChangeListener(editorsTracker);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void uninstalled() {
        finish();
    }

    private void finish() {
        EditorRegistry.removePropertyChangeListener(editorsTracker);
        for (JTextComponent component : EditorRegistry.componentList()) {
            if (!(component instanceof JEditorPane)) {
                return;
            }
            JEditorPane editor = (JEditorPane) component;
            String contentType = editor.getContentType();
            if (!contentType.equals("text/x-java")) {
                return;
            }
            AbbrevDetection.remove(component);
        }
    }
}
