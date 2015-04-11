package com.eltiland.exceptions;

/**
 * Exception for news
 @author Pavel Androschuk
 */
public class NewsException extends Exception {
    public static final String CREATE_ERROR = "Ошибка при создании новости";
    public static final String UPDATE_ERROR = "Ошибка при редактировании новости";
    public static final String DELETE_ERROR = "Ошибка при удалении новости";
    public static final String EMPTY_ENTITY_ERROR = "Не указана новость";
    public static final String EMPTY_TITLE_ERROR = "Не указано название новости";
    public static final String EMPTY_DATE_ERROR = "Не указано дата новости";
    public static final String EMPTY_BODY_ERROR = "Не указан текст новости";

    public NewsException(String message) {
        super(message);
    }
}
