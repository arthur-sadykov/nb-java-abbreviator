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
package com.github.isarthur.netbeans.editor.typingaid.interceptor;

import com.github.isarthur.netbeans.editor.typingaid.JavaAbbreviation;
import com.github.isarthur.netbeans.editor.typingaid.api.Abbreviation;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.typinghooks.DeletedTextInterceptor;
import org.openide.util.Lookup;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaDeletedTextInterceptor implements DeletedTextInterceptor {

    private final Abbreviation abbreviation;

    private JavaDeletedTextInterceptor() {
        this.abbreviation = Lookup.getDefault().lookup(JavaAbbreviation.class);
    }

    @Override
    public boolean beforeRemove(Context context) throws BadLocationException {
        return false;
    }

    @Override
    public void remove(Context context) throws BadLocationException {
        abbreviation.delete();
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
