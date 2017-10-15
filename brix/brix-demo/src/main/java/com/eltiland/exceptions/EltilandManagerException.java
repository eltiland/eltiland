package com.eltiland.exceptions;

/**
 * Generic manager exception.
 */
public class EltilandManagerException extends Exception {

    public static final String ERROR_EMPTY_ENTITY = "Ошибка базы данных - пустой элемент";
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
