package com.eltiland.exceptions;

/**
 * Webinar entity exception
 */
public class WebinarException extends Exception {

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

    public static final String ERROR_WEBINAR_SUB_PAYMENT_UPDATE = "Неизвестная ошибка при сохранении платежа за абонемент";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_CREATE = "Неизвестная ошибка при создании платежа за абонемент";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_SUB_EMPTY = "Ошибка при сохранении платежа за абонемента - не указан абонемент";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_USER_EMPTY = "Ошибка при сохранении платежа за абонемента - не указан пользователь";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_EXISTS = "Ошибка при сохранении платежа за абонемента - для данного пользователя уже существует платеж за данный абонемент";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_NAME_TOO_LONG = "Ошибка при сохранении платежа за абонемента - имя пользователя не может быть больше 255 символов";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_SURNAME_TOO_LONG = "Ошибка при сохранении платежа за абонемента - фамилия пользователя не может быть больше 255 символов";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_PATRONYMIC_TOO_LONG = "Ошибка при сохранении платежа за абонемента - отчество пользователя не может быть больше 255 символов";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_STATUS_EMPTY = "Ошибка при сохранении платежа за абонемента - не указан статус";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_PRICE_EMPTY = "Ошибка при сохранении платежа за абонемента - не указана стоимость";
    public static final String ERROR_WEBINAR_SUB_PAYMENT_REGDATE_EMPTY = "Ошибка при сохранении платежа за абонемента - не указана дата регистрации";

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
