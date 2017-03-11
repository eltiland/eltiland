package com.eltiland.exceptions;

/**
 * Google drive exception.
 */
public class GoogleDriveException extends Exception {

    public static final String ERROR_AUTH = "Ошибка авторизации";
    public static final String ERROR_INSERT = "Ошибка загрузки файла";
    public static final String ERROR_CREATE_FOLDER = "Ошибка создания папки";
    public static final String ERROR_DELETE = "Ошибка удаления файла";
    public static final String ERROR_PERM = "Ошибка установки разрешений";
    public static final String ERROR_GETFILE = "Ошибка получения файла по ID";
    public static final String ERROR_DOWNLOAD = "Ошибка скачивания файла";
    public static final String ERROR_PUBLISH = "Ошибка публикации файла";
    public static final String ERROR_NO_EMPTY_FILE = "Ошибка - отсутствует файл образца документа";

    public static final String ERROR_CONTENT_CREATE = "Ошибка при создании объекта кэширования";
    public static final String ERROR_CONTENT_UPDATE = "Ошибка при изменении объекта кэширования";
    public static final String EROOR_CACHING = "Ошибка при кэшировании";


    /**
     * Default constructor.
     *
     * @param message message
     */
    public GoogleDriveException(String message) {
        super(message);
    }

    /**
     * Constructor with cause.
     *
     * @param message message
     * @param cause   cause
     */
    public GoogleDriveException(String message, Throwable cause) {
        super(message, cause);
    }
}
