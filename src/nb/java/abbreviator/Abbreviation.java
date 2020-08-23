/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator;

/**
 *
 * @author Arthur Sadykov
 */
public class Abbreviation {

    private static Abbreviation instance;
    private int endPosition;
    private final StringBuffer buffer;

    private Abbreviation() {
        this.endPosition = -1;
        this.buffer = new StringBuffer();
    }

    static Abbreviation getInstance() {
        if (instance == null) {
            instance = new Abbreviation();
        }
        return instance;
    }

    int getStartPosition() {
        return endPosition - buffer.length();
    }

    void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    int getEndPosition() {
        return endPosition;
    }

    void append(char character) {
        buffer.append(character);
    }

    void reset() {
        buffer.setLength(0);
        endPosition = -1;
    }

    boolean isEmpty() {
        return buffer.length() == 0;
    }

    String getContent() {
        return buffer.toString();
    }

    void delete() {
        if (!isEmpty()) {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
    }

    int length() {
        return buffer.length();
    }
}
