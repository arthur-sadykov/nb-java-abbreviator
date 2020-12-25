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
package com.github.isarthur.netbeans.editor.typingaid.collector.api;

import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.AbstractAbstractModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.FinalModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.NativeModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.PrivateModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.ProtectedModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.PublicModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.StaticModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.StrictfpModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.SynchronizedModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.TransientModifier;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.impl.VolatileModifier;
import com.github.isarthur.netbeans.editor.typingaid.collector.api.AbstractCodeFragmentCollector;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.StringUtilities;
import com.sun.source.tree.ModifiersTree;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.NATIVE;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.STRICTFP;
import static javax.lang.model.element.Modifier.SYNCHRONIZED;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.element.Modifier.VOLATILE;

/**
 *
 * @author Arthur Sadykov
 */
public abstract class ModifierCollector extends AbstractCodeFragmentCollector {

    protected void collectTopLevelClassModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, FINAL, PUBLIC, STRICTFP);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                case FINAL:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, FINAL));
                    break;
                case PUBLIC:
                    modifiers.remove(PUBLIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectInnerClassModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, FINAL, PUBLIC, PROTECTED, PRIVATE, STRICTFP, STATIC);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                case FINAL:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, FINAL));
                    break;
                case PUBLIC:
                case PROTECTED:
                case PRIVATE:
                    modifiers.removeAll(EnumSet.of(PUBLIC, PROTECTED, PRIVATE));
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectMethodLocalInnerClassModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, FINAL, STRICTFP);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                case FINAL:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, FINAL));
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectTopLevelEnumModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(PUBLIC, STRICTFP);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case PUBLIC:
                    modifiers.remove(PUBLIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectInnerEnumModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(PUBLIC, PROTECTED, PRIVATE, STRICTFP, STATIC);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case PUBLIC:
                case PROTECTED:
                case PRIVATE:
                    modifiers.removeAll(EnumSet.of(PUBLIC, PROTECTED, PRIVATE));
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectTopLevelInterfaceModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, PUBLIC, STRICTFP);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                    modifiers.remove(ABSTRACT);
                    break;
                case PUBLIC:
                    modifiers.remove(PUBLIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectInnerInterfaceModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, PUBLIC, PROTECTED, PRIVATE, STRICTFP, STATIC);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                    modifiers.remove(ABSTRACT);
                    break;
                case PUBLIC:
                case PROTECTED:
                case PRIVATE:
                    modifiers.removeAll(EnumSet.of(PUBLIC, PROTECTED, PRIVATE));
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
                case STRICTFP:
                    modifiers.remove(STRICTFP);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectMethodModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, FINAL, NATIVE, PUBLIC, PROTECTED, PRIVATE, STRICTFP, STATIC,
                SYNCHRONIZED);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, FINAL, NATIVE, PRIVATE, STRICTFP, STATIC, SYNCHRONIZED));
                    break;
                case FINAL:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, FINAL));
                    break;
                case NATIVE:
                case STRICTFP:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, NATIVE, STRICTFP));
                    break;
                case PUBLIC:
                case PROTECTED:
                    modifiers.removeAll(EnumSet.of(PUBLIC, PROTECTED, PRIVATE));
                    break;
                case PRIVATE:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, PUBLIC, PROTECTED, PRIVATE));
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
                case SYNCHRONIZED:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, SYNCHRONIZED));
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectModifiersOfMethodDeclaredInsideInterface(
            ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(ABSTRACT, PUBLIC, STATIC);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case ABSTRACT:
                case STATIC:
                    modifiers.removeAll(EnumSet.of(ABSTRACT, STATIC));
                    break;
                case PUBLIC:
                    modifiers.remove(PUBLIC);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectFieldModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(FINAL, PUBLIC, PROTECTED, PRIVATE, STATIC, TRANSIENT, VOLATILE);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case FINAL:
                case VOLATILE:
                    modifiers.removeAll(EnumSet.of(FINAL, VOLATILE));
                    break;
                case PUBLIC:
                case PROTECTED:
                case PRIVATE:
                    modifiers.removeAll(EnumSet.of(PUBLIC, PROTECTED, PRIVATE));
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
                case TRANSIENT:
                    modifiers.remove(TRANSIENT);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectModifiersOfFieldDeclaredInsideInterface(
            ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(FINAL, PUBLIC, STATIC);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case FINAL:
                    modifiers.remove(FINAL);
                    break;
                case PUBLIC:
                    modifiers.remove(PUBLIC);
                    break;
                case STATIC:
                    modifiers.remove(STATIC);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    protected void collectVariableModifiers(ModifiersTree modifiersTree, CodeCompletionRequest request) {
        Set<Modifier> modifiers = EnumSet.of(FINAL);
        for (Modifier modifier : modifiersTree.getFlags()) {
            switch (modifier) {
                case FINAL:
                    modifiers.remove(FINAL);
                    break;
            }
        }
        filterAndCollect(request, modifiers);
    }

    private void filterAndCollect(CodeCompletionRequest request, Set<Modifier> modifiers) {
        filterByAbbreviation(request, modifiers);
        collect(request, modifiers);
    }

    private void filterByAbbreviation(CodeCompletionRequest request, Set<Modifier> modifiers) {
        modifiers.removeIf(modifier -> {
            String modifierAbbreviation = StringUtilities.getElementAbbreviation(modifier.toString().toLowerCase());
            return !request.getAbbreviation().getContent().equals(modifierAbbreviation);
        });
    }

    private void collect(CodeCompletionRequest request, Set<Modifier> modifiers) {
        List<CodeFragment> codeFragments = request.getCodeFragments();
        modifiers.forEach(modifier -> codeFragments.add(createModifier(modifier)));
    }

    private com.github.isarthur.netbeans.editor.typingaid.codefragment.modifier.api.Modifier createModifier(
            Modifier modifier) {
        switch (modifier) {
            case ABSTRACT:
                return new AbstractAbstractModifier();
            case FINAL:
                return new FinalModifier();
            case NATIVE:
                return new NativeModifier();
            case PRIVATE:
                return new PrivateModifier();
            case PROTECTED:
                return new ProtectedModifier();
            case PUBLIC:
                return new PublicModifier();
            case STATIC:
                return new StaticModifier();
            case STRICTFP:
                return new StrictfpModifier();
            case SYNCHRONIZED:
                return new SynchronizedModifier();
            case TRANSIENT:
                return new TransientModifier();
            case VOLATILE:
                return new VolatileModifier();
            default:
                return null;
        }
    }
}
