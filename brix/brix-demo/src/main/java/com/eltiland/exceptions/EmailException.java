package com.eltiland.exceptions;

/**
 * @author knorr
 * @version 1.0
 * @since 7/24/12
 */
public class EmailException extends Exception {
    public static final String CREATE_ERROR = "Ошибка при создании письма";
    public static final String UPDATE_ERROR = "Ошибка при редактировании письма";
    public static final String DELETE_ERROR = "Ошибка при удалении письма";
    public static final String SEND_MAIL_ERROR = "Ошибка при отправке письма";


    public EmailException(String message) {
        super(message);
    }

    /**
     * Constructor with cause.
     *
     * @param message message
     * @param cause   cause
     */
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
