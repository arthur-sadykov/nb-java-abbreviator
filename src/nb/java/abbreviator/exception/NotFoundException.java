/*
 * Copyright (c) 2020 Arthur Sadykov.
 */
package nb.java.abbreviator.exception;

/**
 *
 * @author Arthur Sadykov
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
