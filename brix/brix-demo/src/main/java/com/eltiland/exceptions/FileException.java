package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class FileException extends Exception {

    public static final String ERROR_FILE_INSTANCE = "Внутренняя ошибка файла";
    public static final String ERROR_FILE_NAME = "Ошибка - Неправильное имя файла";
    public static final String ERROR_FILE_CREATE = "Неизвестная ошибка при создании файла";
    public static final String ERROR_FILE_REMOVE = "Неизвестная ошибка при удалении файла";

    public FileException() {
    }

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileException(Throwable cause) {
        super(cause);
    }
}
