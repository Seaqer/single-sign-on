package interfaces.utils;


/**
 * Интерфейс для проверки вводимых данных на корректность
 */
public interface Validator {

    /**
     * Проверить вводимые данные
     * @param data данные для проверки
     * @return результат проверки
     */
    boolean validate(String data);
}
