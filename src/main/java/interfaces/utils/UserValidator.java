package interfaces.utils;


/**
 *  * Интерфейс для проверки атрибутов пользователя на корректность
 */
public interface UserValidator {
    /**
     * Проверить все атрибуты
     * @param login Логин
     * @param password Пароль
     * @return результат проверки
     */
    boolean validate(String login, String password);

    /**
     * Проверить логин пользователя
     * @param login Логин
     * @return результат проверки
     */
    boolean validateLogin(String login);

    /**
     * Проверить пароль пользователя
     * @param password пароль
     * @return результат проверки
     */
    boolean validatePassword(String password);
}
