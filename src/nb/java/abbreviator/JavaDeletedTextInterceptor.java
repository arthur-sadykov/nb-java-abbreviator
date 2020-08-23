/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.typinghooks.DeletedTextInterceptor;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaDeletedTextInterceptor implements DeletedTextInterceptor {

    private final Abbreviation abbreviation;

    private JavaDeletedTextInterceptor() {
        this.abbreviation = Abbreviation.getInstance();
    }

    @Override
    public boolean beforeRemove(Context context) throws BadLocationException {
        return false;
    }

    @Override
    public void remove(Context context) throws BadLocationException {
        int offset = context.getOffset();
        abbreviation.delete();
        abbreviation.setEndPosition(offset - 1);
    }

    @Override
    public void afterRemove(Context context) throws BadLocationException {
    }

    @Override
    public void cancelled(Context context) {
    }

    @MimeRegistration(mimeType = "text/x-java", service = DeletedTextInterceptor.Factory.class)
    public static class FactoryImpl implements DeletedTextInterceptor.Factory {

        @Override
        public DeletedTextInterceptor createDeletedTextInterceptor(MimePath mimePath) {
            return new JavaDeletedTextInterceptor();
        }
    }
}
