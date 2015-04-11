package com.eltiland.exceptions;

/**
 * Exception for Faq, FaqApproval, FaqCategory models
 *
 @author Pavel Androschuk
 */
public class FaqException extends Exception {
    public static final String CREATE_FAQ_ERROR = "Ошибка при создании FAQ";
    public static final String CHANGE_FAQ_ORDER_ERROR = "Ошибка при изменении порядка FAQ";
    public static final String DELETE_FAQ_ERROR = "Ошибка при удалении FAQ";
    public static final String EDIT_FAQ_ERROR = "Ошибка при редактировании FAQ";
    public static final String FAQ_NULL_ENTITY_ERROR = "Не указан FAQ";
    public static final String FAQ_QUESTION_EMPTY = "Не указан вопрос для FAQ";
    public static final String FAQ_ANSWER_EMPTY = "Не указан ответ для FAQ";
    public static final String FAQ_QUESTION_TOO_LONG = "Слишком много символов в вопросе FAQ";
    public static final String FAQ_ANSWER_TOO_LONG = "Слишком много символов в ответе FAQ";

    public static final String CREATE_FAQ_APPROVAL_ERROR = "Ошибка при создании вопроса";
    public static final String EDIT_FAQ_APPROVAL_ERROR = "Ошибка при редактировании вопроса";
    public static final String DELETE_FAQ_APPROVAL_ERROR = "Ошибка при удалении вопроса";
    public static final String FAQ_APPROVAL_NULL_ENTITY_ERROR = "Не указан вопрос";
    public static final String FAQ_APPROVAL_EMAIL_EMPTY = "Не указан электронный адрес";
    public static final String FAQ_APPROVAL_QUESTION_EMPTY = "Не указан вопрос";
    public static final String FAQ_APPROVAL_QUESTION_TOO_LONG = "Слишком много символов в вопросе";
    public static final String FAQ_APPROVAL_ANSWER_TOO_LONG = "Слишком много символов в ответе";
    public static final String FAQ_APPROVAL_ANSWER_EMPTY = "Не указан ответ";

    public static final String CREATE_CATEGORY_ERROR = "Ошибка при создании категории";
    public static final String CHANGE_CATEGORY_ORDER_ERROR = "Ошибка при изменении порядка категории";
    public static final String DELETE_CATEGORY_ERROR = "Ошибка при удалении категории";
    public static final String EDIT_CATEGORY_ERROR = "Ошибка при редактировании категории";
    public static final String CATEGORY_NULL_ENTITY_ERROR = "Не указана категория";
    public static final String CATEGORY_NAME_EMPTY = "Не указано название категории";
    public static final String CATEGORY_NAME_TOO_LONG = "Слишком много символов в названии категории";

    public FaqException(String message) {
        super(message);
    }
}
