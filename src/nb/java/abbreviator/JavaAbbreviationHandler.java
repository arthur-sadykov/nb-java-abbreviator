/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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
                } else {
                    return insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation);
                }
            } else {
                return insertStaticMethodSelection(expressionAbbreviation, identifierAbbreviation);
            }
        } else {
        }
        return false;
    }

    private boolean insertMethodSelection(List<Element> elements, String methodAbbreviation) {
        return helper.insertMethodSelection(elements, methodAbbreviation);
    }

    private boolean insertStaticMethodSelection(String expressionAbbreviation, String methodAbbreviation) {
        List<TypeElement> typeElements = helper.getTypeElementsByAbbreviation(expressionAbbreviation);
        return helper.insertStaticMethodSelection(typeElements, methodAbbreviation);
    }

    Document getDocument() {
        return document;
    }
}
