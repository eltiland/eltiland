package com.eltiland.exceptions;

/**
 * Generic manager exception.
 */
public class EltilandManagerException extends Exception {
    /**
     * Default constructor.
     *
     * @param message message
     */
    public EltilandManagerException(String message) {
        super(message);
    }

    /**
     * Constructor with cause.
     *
     * @param message message
     * @param cause   cause
     */
    public EltilandManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
