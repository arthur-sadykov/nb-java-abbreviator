/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.util.List;
import javax.lang.model.element.Element;
import javax.swing.text.Document;

/**
 *
 * @author Arthur Sadykov
 */
public class JavaAbbreviationHandler {

    private final JavaSourceHelper helper;
    private final Document document;

    public JavaAbbreviationHandler(JavaSourceHelper helper) {
        this.helper = helper;
        this.document = helper.getDocument();
    }

    void collectLocalElements(int position) {
        helper.collectLocalElements(position);
    }

    boolean process(Abbreviation abbreviation) {
        String abbreviationContent = abbreviation.getContent();
        helper.setTypedAbbreviation(abbreviationContent);
        helper.collectLocalElements(abbreviation.getStartPosition());
        if (abbreviation.getContent().contains(".")) {
            String expressionAbbreviation = abbreviationContent.substring(0, abbreviationContent.indexOf('.'));
            String identifierAbbreviation = abbreviationContent.substring(abbreviationContent.indexOf('.') + 1);
            List<Element> elements = helper.getElementsByAbbreviation(expressionAbbreviation);
            if (!elements.isEmpty()) {
                if (insertMethodSelection(elements, identifierAbbreviation)) {
                    return true;
                } else if (insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation)) {
                    return true;
                } else {
                    return insertConstantSelection(expressionAbbreviation, identifierAbbreviation);
                }
            } else {
                if (insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation)) {
                    return true;
                } else {
                    return insertConstantSelection(expressionAbbreviation, identifierAbbreviation);
                }
            }
        } else {
            if (isMemberSelection()) {
                return insertChainedMethodSelection(abbreviationContent);
            } else if (isAnnotationSelection()) {
                return insertAnnotation(abbreviationContent);
            } else {
                List<Element> elements = helper.getElementsByAbbreviation(abbreviationContent);
                if (insertLocalElement(elements)) {
                    return true;
                } else if (insertKeyword(abbreviationContent)) {
                    return true;
                } else if (insertLocalMethod(abbreviationContent)) {
                    return true;
                } else {
                    return insertType(abbreviationContent);
                }
            }
        }
    }

    private boolean insertMethodSelection(List<Element> elements, String methodAbbreviation) {
        return helper.insertMethodSelection(elements, methodAbbreviation);
    }

    private boolean insertStaticMethodSelection(String typeAbbreviation, String methodAbbreviation) {
        return helper.insertStaticMethodSelection(typeAbbreviation, methodAbbreviation);
    }

    private boolean insertLocalElement(List<Element> elements) {
        return helper.insertLocalElement(elements);
    }

    private boolean insertKeyword(String keywordAbbreviation) {
        return helper.insertKeyword(keywordAbbreviation);
    }

    private boolean insertLocalMethod(String methodAbbreviation) {
        return helper.insertLocalMethod(methodAbbreviation);
    }

    Document getDocument() {
        return document;
    }

    private boolean isMemberSelection() {
        return helper.isMemberSelection();
    }

    private boolean insertChainedMethodSelection(String methodAbbreviation) {
        return helper.insertChainedMethodSelection(methodAbbreviation);
    }

    private boolean isAnnotationSelection() {
        return helper.isAnnotationSelection();
    }

    private boolean insertAnnotation(String annotationAbbreviation) {
        return helper.insertAnnotation(annotationAbbreviation);
    }

    private boolean insertConstantSelection(String expressionAbbreviation, String constantAbbreviation) {
        return helper.insertConstantSelection(expressionAbbreviation, constantAbbreviation);
    }

    private boolean insertType(String typeAbbreviation) {
        return helper.insertType(typeAbbreviation);
    }
}
