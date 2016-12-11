package domain.exceptions;


/**
 * Ошибка приложения создания пользователя
 */
public class SSOException extends RuntimeException {

    /**
     * Конструктор приложения
     *
     * @param message сообщение
     */
    public SSOException(String message) {
        super(message);
    }

    /**
     * Конструктор приложения
     *
     * @param exception исключение
     */
    public SSOException(Exception exception) {
        this("ошибка сервиса регистрации", exception);
    }

    /**
     * Конструктор приложения
     *
     * @param message   сообщение
     * @param exception исключение
     */
    public SSOException(String message, Exception exception) {
        super(message, exception);
    }
}
