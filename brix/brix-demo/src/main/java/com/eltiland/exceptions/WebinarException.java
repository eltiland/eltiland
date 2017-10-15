package com.eltiland.exceptions;

/**
 * Webinar entity exception
 */
public class WebinarException extends Exception{

    public static final String ERROR_WEBINAR_EVENT_NAME_EMPTY = "Ошибка при создании мероприятия - имя не может быть пустым";
    public static final String ERROR_WEBINAR_EVENT_CREATE_PARAMS = "Ошибка при создании мероприятия - неправильный формат переданных параметров";
    public static final String ERROR_WEBINAR_EVENT_CREATE_AUTH = "Ошибка при создании мероприятия - ошибка авторизации";
    public static final String ERROR_WEBINAR_EVENT_CREATE = "Ошибка при создании мероприятия";

    public static final String ERROR_WEBINAR_SUB_CREATE_NAME_EMPTY = "Ошибка при сохранении абонемента - имя не может быть пустым";
    public static final String ERROR_WEBINAR_SUB_CREATE_NAME_TOO_LONG = "Ошибка при сохранении абонемента - имя не может быть больше 255 символов";
    public static final String ERROR_WEBINAR_SUB_CREATE_INFO_TOO_LONG = "Ошибка при сохранении абонемента - описание не может быть больше 1024 символов";
    public static final String ERROR_WEBINAR_SUB_CREATE_WEBINARS_EMPTY = "Ошибка при создании абонемента - список вебинаров не может быть пустым";
    public static final String ERROR_WEBINAR_SUB_CREATE = "Неизвестная ошибка при создании абонемента";
    public static final String ERROR_WEBINAR_SUB_UPDATE = "Неизвестная ошибка при сохранении абонемента";

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
