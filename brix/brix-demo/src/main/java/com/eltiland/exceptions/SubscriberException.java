package com.eltiland.exceptions;

/**
 * Exception class for Subscriber
 * @author Pavel Androschuk
 */
public class SubscriberException extends Exception {
    public static final String CREATE_ERROR = "Ошибка при создании подписчика";
    public static final String DELETE_ERROR = "Ошибка при удалении подписчика";
    public static final String UPDATE_ERROR = "Ошибка при редактировании подписчика";
    public static final String EMPTY_ENTITY_ERROR = "Не указан подписчик";
    public static final String EMPTY_EMAIL_ERROR = "Не указан электронный адрес подписчика";
    public static final String EMPTY_UNSUBSRIBE_LINK_ERROR = "Не указана ссылка отписки для подписчика";

    public SubscriberException(String message) {
        super(message);
    }
}
