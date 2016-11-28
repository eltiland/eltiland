package com.eltiland.exceptions;

/**
 * Webinar entity exception
 */
public class WebinarException extends Exception{

    public static final String ERROR_WEBINAR_EVENT_NAME_EMPTY = "Ошибка при создании мероприятия - имя не может быть пустым";
    public static final String ERROR_WEBINAR_EVENT_CREATE_PARAMS = "Ошибка при создании мероприятия - неправильный формат переданных параметров";
    public static final String ERROR_WEBINAR_EVENT_CREATE_AUTH = "Ошибка при создании мероприятия - ошибка авторизации";
    public static final String ERROR_WEBINAR_EVENT_CREATE = "Ошибка при создании мероприятия";

    public WebinarException() {
    }

    public WebinarException(String message) {
        super(message);
    }

    public WebinarException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebinarException(Throwable cause) {
        super(cause);
    }
}
