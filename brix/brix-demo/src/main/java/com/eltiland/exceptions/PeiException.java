package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class PeiException extends Exception {
    public static final String CREATE_ERROR = "Ошибка при добавлении ДОУ";
    public static final String SAVE_AVATAR_ERROR = "Ошибка при сохранении аватара ДОУ";
    public static final String UPDATE_ERROR = "Ошибка при редактировании ДОУ";
    public static final String DELETE_ERROR = "Ошибка при удалении ДОУ";
    public static final String EMPTY_ENTITY = "Не указан ДОУ";
    public static final String EMPTY_NAME_ERROR = "Не указано название ДОУ";
    public static final String EMPTY_ADDRESS_ERROR = "Не указан адрес ДОУ";
    public static final String EMPTY_EMAIL_ERROR = "Не указана электронная почта ДОУ";
    public static final String EMPTY_AVATAR_ERROR = "Не указан аватар для ДОУ";

    public PeiException(String message) {
        super(message);
    }
}
