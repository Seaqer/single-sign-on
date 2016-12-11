package interfaces.utils;


/**
 * Менеджер паролей
 */
public interface PasswordManager {
    /**
     * Получить преобразованный пароль
     * @param data пароль
     * @return преобразованное значение
     */
    String getPassword(String data);
}
