package com.eltiland.exceptions;

/**
 * @author Aleksey Plotnikov
 * @version 1.0
 * @since 7/24/12
 */
public class CourseException extends Exception {

    public static final String ERROR_COURSE_EMPTY_NAME = "Имя курса не может быть пустым";
    public static final String ERROR_COURSE_NAME_TOO_LONG = "Длина имени курса не может превышать 255 символов";
    public static final String ERROR_COURSE_NAME_NOT_UNIQUE = "Имя курса должно быть уникальным";
    public static final String ERROR_COURSE_EMPTY_AUTHOR = "Не задан автор курса";
    public static final String ERROR_COURSE_EMPTY_CREATION_DATE = "Дата создания курса не может быть пуста";
    public static final String ERROR_COURSE_EMPTY_CONFIRMATION_FLAG = "Флаг подтверждения пользователя не может быть пустым";
    public static final String ERROR_COURSE_EMPTY_STATUS = "Статус не может быть пустым";
    public static final String ERROR_DAYS_INCORRECT = "Некорректное значение срока доступа к курсу";
    public static final String ERROR_DAYS_FREE = "Срок доступа к курсу не может быть установлен при нулевой цене курса";

    public static final String ERROR_AUTHOR_COURSE_EMPTY_INDEX = "Для авторского курса индекс не может быть пустым";

    public static final String ERROR_TRAINING_COURSE_JOIN_DATE_EMPTY = "Для КПК дата начала подачи заявок не может быть пуста";
    public static final String ERROR_TRAINING_COURSE_START_DATE_EMPTY = "Для КПК дата начала обучения не может быть пуста";
    public static final String ERROR_TRAINING_COURSE_END_DATE_EMPTY = "Для КПК дата окончания обучения не может быть пуста";

    public static final String ERROR_COURSE_CREATE = "Неизвестная ошибка при создании курса.";
    public static final String ERROR_COURSE_REMOVE = "Неизвестная ошибка при удалении курса.";
    public static final String ERROR_COURSE_UPDATE = "Неизвестная ошибка при сохранении курса.";

    public static final String ERROR_USERDATA_EMPTY_COURSE = "Название курса не должно быть пустым";
    public static final String ERROR_USERDATA_EMPTY_TYPE = "Тип регистрационных данных не может быть пустым";
    public static final String ERROR_USERDATA_EMPTY_STATUS = "Статус регистрационных данных не может быть пустым";
    public static final String ERROR_USERDATA_CAPTION_TOO_LONG = "Длина подсказки для поля не должна превышать 128 символов";

    public static final String ERROR_USERDATA_CREATE = "Неизвестная ошибка при создании регистрационных данных.";
    public static final String ERROR_USERDATA_UPDATE = "Неизвестная ошибка при редактировании регистрационных данных.";
    public static final String ERROR_USERDATA_DELETE = "Неизвестная ошибка при удалении регистрационных данных.";

    public static final String ERROR_COURSEADMIN_USER_EMPTY = "Не указан пользователь для администратора курса.";
    public static final String ERROR_COURSEADMIN_COURSE_EMPTY = "Не указан курс для администратора курса.";
    public static final String ERROR_COURSEADMIN_USER_AUTHOR = "Автор курса не может быть добавлен как администратор курса.";
    public static final String ERROR_COURSEADMIN_USER_EXISTS = "Пользователь %s уже является администратором курса.";
    public static final String ERROR_COURSEADMIN_CREATE = "Неизвестная ошибка при создании администратора курса.";

    public static final String ERROR_BLOCK_COURSE_EMPTY = "У блока должен был установлен курс.";
    public static final String ERROR_BLOCK_COURSE_INCORRECT = "Не определена принадлежность блока (демо/полная версия).";
    public static final String ERROR_BLOCK_INDEX_EMPTY = "Индекс блока курса не может быть пустым.";
    public static final String ERROR_BLOCK_INDEX_INCORRECT = "Индекс блока курса не может быть меньше 1";
    public static final String ERROR_BLOCK_NAME_EMPTY = "Имя блока курса не может быть пустым.";
    public static final String ERROR_BLOCK_NAME_TOO_LONG = "Длина названия блока курса не может превышать 128 символов.";
    public static final String ERROR_BLOCK_CREATE = "Неизвестная ошибка при создании блока курса.";
    public static final String ERROR_BLOCK_UPDATE = "Неизвестная ошибка при сохранении блока курса.";
    public static final String ERROR_BLOCK_INCORRECT_DATE = "Некорректная дата доступа к блоку курса.";
    public static final String ERROR_BLOCK_NOT_FULL_DATE = "Дата доступа к блоку курса должна быть установлена полностью либо сброшена";
    public static final String ERROR_BLOCK_DEFAULT_ACCESS_EMPTY = "Доступ к блоку по умолчанию не может быть пустым.";

    public static final String ERROR_BLOCK_ACCESS_EMPTY_BLOCK = "Не установлен блок курса.";
    public static final String ERROR_BLOCK_ACCESS_EMPTY_LISTENER = "Не установлен пользователь.";
    public static final String ERROR_BLOCK_ACCESS_ALREADY_SET = "Доступ для пользователя %s уже определен.";
    public static final String ERROR_BLOCK_ACCESS_INCORRECT_DATES = "Неправильный промежуток дат доступа.";
    public static final String ERROR_BLOCK_ACCESS_EMPTY_DATES = "Должны быть установлены либо сброшены как дата начала доступа так и дата окончания.";
    public static final String ERROR_BLOCK_ACCESS_CLOSED_DATES = "При закрытом доступе не может быть установлен промежуток доступа.";
    public static final String ERROR_BLOCK_ACCESS_CREATE = "Неизвестная ошибка при создании элемента прав доступа к блоку курса.";
    public static final String ERROR_BLOCK_ACCESS_UPDATE = "Неизвестная ошибка при сохранении элемента прав доступа к блоку курса.";

    public static final String ERROR_ITEM_BLOCK_EMPTY = "Для курса должен быть установлен либо блок, либо группа.";
    public static final String ERROR_ITEM_NAME_EMPTY = "Не установлено название элемента курса.";
    public static final String ERROR_ITEM_NAME_TOO_LONG = "Длина названия элемента курса не может превышать 128 символов.";
    public static final String ERROR_ITEM_INDEX_EMPTY = "Не установлен индекс элемента курса.";
    public static final String ERROR_ITEM_INDEX_INCORRECT = "Индекс элемента курса не может быть меньше 0.";
    public static final String ERROR_ITEM_DOCUMENT_EMPTY = "Не установлен Google контент для элемента курса 'документ' либо 'презентация'";
    public static final String ERROR_ITEM_WEBINAR_EMPTY = "Не установлен Вебинар для элемента курса 'вебинар'";
    public static final String ERROR_ITEM_CREATE = "Неизвестная ошибка при создании элемента курса";
    public static final String ERROR_ITEM_UPDATE = "Неизвестная ошибка при сохранении элемента курса";

    public static final String ERROR_VIDEO_ITEM_NAME_EMPTY = "Название видео не может быть пустым";
    public static final String ERROR_VIDEO_ITEM_ITEM_EMPTY = "Элемент курса не установлен для видео";
    public static final String ERROR_VIDEO_ITEM_NAME_TOO_LONG = "Длина названия видео не может превышать 1024 символа";
    public static final String ERROR_VIDEO_ITEM_DESC_TOO_LONG = "Длина описания видео не может превышать 1024 символа";
    public static final String ERROR_VIDEO_ITEM_LINK_TOO_LONG = "Длина ссылки видео не может превышать 64 символа";
    public static final String ERROR_VIDEO_ITEM_LINK_EMPTY = "Ссылка видео не может быть пуста";
    public static final String ERROR_VIDEO_ITEM_INDEX_EMPTY = "Индекс видео не может быть пуст";
    public static final String ERROR_VIDEO_ITEM_CREATE = "Неизвестная ошибка при создании элемента видео";
    public static final String ERROR_VIDEO_ITEM_UPDATE = "Неизвестная ошибка при сохранени элемента видео";
    public static final String ERROR_VIDEO_ITEM_DELETE = "Неизвестная ошибка при удалении элемента видео";

    public static final String ERROR_LISTENER_COURSE_EMPTY = "Курс не может быть пустым у слушателя";
    public static final String ERROR_LISTENER_USER_EMPTY = "Пользователь не может быть пустым у слушателя";
    public static final String ERROR_LISTENER_ALREADY_EXISTS = "В системе уже присутствует заявка слушателя %s на данный курс";
    public static final String ERROR_LISTENER_STATUS_EMPTY = "Статус слушателя не может быть пустым";
    public static final String ERROR_LISTENER_OFFER_TOO_LONG = "Длина оферты не может быть больше 128 символов.";
    public static final String ERROR_LISTENER_REQUISITES_TOO_LONG = "Длина реквизитов оплаты не может быть больше 4096 символов.";
    public static final String ERROR_LISTENER_TYPE_EMPTY = "Тип слушателя не может быть пустым.";
    public static final String ERROR_LISTENER_CREATE = "Неизвестная ошибка при создании слушателя курса";
    public static final String ERROR_LISTENER_DELETE = "Неизвестная ошибка при удалении слушателя курса";
    public static final String ERROR_LISTENER_UPDATE = "Неизвестная ошибка при сохранении слушателя курса";

    public static final String ERROR_PRINTSTAT_ITEM_EMPTY = "Элемент курса для статистики печати не может быть пустым";
    public static final String ERROR_PRINTSTAT_LISTENER_EMPTY = "Слушатель для статистики печати не может быть пустым";
    public static final String ERROR_PRINTSTAT_NULL_VALUE = "Данные статистики печати не могут быть пустыми";
    public static final String ERROR_PRINTSTAT_NEGATIVE_VALUE = "Данные статистики печати не могут быть отрицательными";
    public static final String ERROR_PRINTSTAT_WRONG_VALUE = "Данные статистики печати для пользователя не может быть больше лимита печати";
    public static final String ERROR_PRINTSTAT_CREATE = "Неизвестная ошибка при создании статистики печати элемента курса";
    public static final String ERROR_PRINTSTAT_DELETE = "Неизвестная ошибка при удалении статистики печати элемента курса";
    public static final String ERROR_PRINTSTAT_UPDATE = "Неизвестная ошибка при сохранении статистики печати элемента курса";

    public static final String ERROR_CONTENT_CREATE = "Неизвестная ошибка при создании контента элемента курса";
    public static final String ERROR_CONTENT_UPDATE = "Неизвестная ошибка при сохранении контента элемента курса";

    public CourseException() {
    }

    public CourseException(String message) {
        super(message);
    }

    public CourseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CourseException(Throwable cause) {
        super(cause);
    }
}
