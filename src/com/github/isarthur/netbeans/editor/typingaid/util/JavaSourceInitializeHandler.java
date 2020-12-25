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
package com.github.isarthur.netbeans.editor.typingaid.util;

import com.github.isarthur.netbeans.editor.typingaid.constants.ConstantDataManager;
import java.io.IOException;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaSourceInitializeHandler {

    public static JavaSource getJavaSourceForDocument(Document document) {
        JavaSource javaSource = JavaSource.forDocument(document);
        if (javaSource == null) {
            throw new IllegalStateException(ConstantDataManager.JAVA_SOURCE_NOT_ASSOCIATED_TO_DOCUMENT);
        }
        return javaSource;
    }

    public static JavaSource getJavaSourceForFileObject(FileObject file) {
        JavaSource javaSource = JavaSource.forFileObject(file);
        if (javaSource == null) {
            throw new IllegalStateException(ConstantDataManager.JAVA_SOURCE_NOT_ASSOCIATED_TO_DOCUMENT);
        }
        return javaSource;
    }

    public static void moveStateToParsedPhase(CompilationController controller) throws IOException {
        JavaSource.Phase phase = controller.toPhase(JavaSource.Phase.PARSED);
        if (phase.compareTo(JavaSource.Phase.PARSED) < 0) {
            throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_PARSED_PHASE);
        }
    }

    public static void moveStateToResolvedPhase(CompilationController controller) {
        JavaSource.Phase phase;
        try {
            phase = controller.toPhase(JavaSource.Phase.RESOLVED);
            if (phase.compareTo(JavaSource.Phase.RESOLVED) < 0) {
                throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_RESOLVED_PHASE);
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ConstantDataManager.STATE_IS_NOT_IN_RESOLVED_PHASE);
        }
    }
}
