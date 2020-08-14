/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

import com.sun.source.tree.ExpressionTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodSelectionWrapper {

    private final Element element;
    private final ExecutableElement method;
    private final List<ExpressionTree> arguments;
    private final int argumentsNumber;
    private int resolvedArgumentsNumber;
    private double relation;

    public MethodSelectionWrapper(Element element, ExecutableElement method, List<ExpressionTree> arguments) {
        this.element = element;
        this.method = method;
        this.arguments = arguments;
        this.argumentsNumber = method.getParameters().size();
    }

    public Element getElement() {
        return element;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public List<ExpressionTree> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public int getArgumentsNumber() {
        return argumentsNumber;
    }

    public int getResolvedArgumentsNumber() {
        return resolvedArgumentsNumber;
    }

    public double getRelation() {
        return relation;
    }

    public void setResolvedArgumentsNumber(int resolvedArgumentsNumber) {
        this.resolvedArgumentsNumber = resolvedArgumentsNumber;
        this.relation = argumentsNumber == 0 ? 0 : this.resolvedArgumentsNumber / argumentsNumber;
    }
}
