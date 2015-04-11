package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class TestException extends Exception {
    public TestException() {
    }

    public TestException(String message) {
        super(message);
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestException(Throwable cause) {
        super(cause);
    }
}
