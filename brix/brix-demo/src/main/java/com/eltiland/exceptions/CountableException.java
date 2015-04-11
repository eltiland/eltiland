package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class CountableException extends Exception {

    public static final String ERROR_COUNTABLE_NEW = "Ошибка присваивания нового индекса";
    public static final String ERROR_COUNTABLE_DELETE = "Ошибка удаления индексируемой сущности";
    public static final String ERROR_COUNTABLE_MOVE = "Ошибка изменения индекса сущности";

    public static final String ERROR_COURSE_NAME_TOO_LONG = "Длина имени курса не может превышать 255 символов";
    public static final String ERROR_COURSE_NAME_NOT_UNIQUE = "Имя курса должно быть уникальным";
    public static final String ERROR_COURSE_EMPTY_AUTHOR = "Не задан автор курса";
    public static final String ERROR_COURSE_EMPTY_CREATION_DATE = "Дата создания курса не может быть пуста";
    public static final String ERROR_COURSE_EMPTY_CONFIRMATION_FLAG = "Флаг подтверждения пользователя не может быть пустым";
    public static final String ERROR_COURSE_EMPTY_STATUS = "Статус не может быть пустым";

    public static final String ERROR_AUTHOR_COURSE_EMPTY_INDEX = "Для авторского курса индекс не может быть пустым";

    public static final String ERROR_TRAINING_COURSE_JOIN_DATE_EMPTY = "Для КПК дата начала подачи заявок не может быть пуста";
    public static final String ERROR_TRAINING_COURSE_START_DATE_EMPTY = "Для КПК дата начала обучения не может быть пуста";
    public static final String ERROR_TRAINING_COURSE_END_DATE_EMPTY = "Для КПК дата окончания обучения не может быть пуста";
    public static final String ERROR_TRAINING_COURSE_INCORRECT_INTERVAL = "Для КПК дата начала обучениея должна быть в будущем и до даты окончания обучения";
    public static final String ERROR_TRAINING_COURSE_INCORRECT_JOIN_DATE = "Для КПК дата начала подачи заявок должна быть в будущем и до даты начала обучения";

    public static final String ERROR_COURSE_CREATE = "Неизвестная ошибка при создании курса.";
    public static final String ERROR_COURSE_REMOVE = "Неизвестная ошибка при удалении курса.";
    public static final String ERROR_COURSE_UPDATE = "Неизвестная ошибка при сохранении курса.";

    public static final String ERROR_USERDATA_EMPTY_COURSE = "Название курса не должно быть пустым";
    public static final String ERROR_USERDATA_EMPTY_TYPE = "Тип регистрационных данных не может быть пустым";
    public static final String ERROR_USERDATA_EMPTY_STATUS = "Статус регистрационных данных не может быть пустым";
    public static final String ERROR_USERDATA_CAPTION_TOO_LONG = "Длина подсказки для поля не должна превышать 128 символов";

    public static final String ERROR_USERDATA_CREATE = "Неизвестная ошибка при создании регистрационных данных.";
    public static final String ERROR_USERDATA_UPDATE = "Неизвестная ошибка при редактировании регистрационных данных.";


    public CountableException() {
    }

    public CountableException(String message) {
        super(message);
    }

    public CountableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CountableException(Throwable cause) {
        super(cause);
    }
}
