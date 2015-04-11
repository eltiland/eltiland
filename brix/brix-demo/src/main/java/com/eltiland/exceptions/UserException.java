package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class UserException extends Exception {

    public static final String ERROR_USER_UPDATE = "Неизвестная ошибка при обновлении пользователя";

    public static final String ERROR_USERFILE_OWNER_EMPTY = "Не установлен владелец у файла пользователя";
    public static final String ERROR_USERFILE_FILE_EMPTY = "Не установлен файл и сущности 'файл пользователя'";
    public static final String ERROR_USERFILE_COURSE_EMPTY = "Не установлен курс и сущности 'файл пользователя'";
    public static final String ERROR_USERFILE_UPLOAD_DATE_EMPTY = "Не установлена дата загрузки у файла пользователя";
    public static final String ERROR_USERFILE_CREATE = "Неизвестная ошибка при создании файла пользователя";
    public static final String ERROR_USERFILE_DELETE = "Неизвестная ошибка при удалении файла пользователя";
    public static final String ERROR_USERFILE_UPDATE = "Неизвестная ошибка при обновлении файла пользователя";

    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }
}
