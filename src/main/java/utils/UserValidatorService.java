package utils;

import domain.exceptions.SSOException;
import interfaces.utils.UserValidator;
import org.apache.log4j.Logger;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Класс для проверки атрибутов пользователя на корректность
 */
public class UserValidatorService implements UserValidator {
    private static final Logger LOGGER = Logger.getLogger(UserValidatorService.class);

    /**
     * Проверить все атрибуты
     * @param login Логин
     * @param password Пароль
     * @return результат проверки
     */
    @Override
    public boolean validate(String login, String password) {
        return validateLogin(login) && validatePassword(password);
    }

    /**
     * Проверить логин пользователя
     * @param login Логин
     * @return результат проверки
     */
    @Override
    public boolean validateLogin(String login) {
        if (Objects.isNull(login)) {
            LOGGER.trace("UserValidatorService:validateLogin - data:null");
            throw new SSOException("поле логина не может быть пустым");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{4,20}$");
        Matcher match = pattern.matcher(login);
        if (!match.find()) {
            LOGGER.trace("UserValidatorService:validateLogin Логин должен состоять из символов и цифр, начинаться с буквы и иметь длинну от 5 до 20 символов");
            throw new SSOException("Логин должен состоять из символов и цифр, начинаться с буквы и иметь длинну от 5 до 20 символов");
        }
        return true;
    }

    /**
     * Проверить пароль пользователя
     * @param password пароль
     * @return результат проверки
     */
    @Override
    public boolean validatePassword(String password) {
        if (Objects.isNull(password)) {
            LOGGER.trace("UserValidatorService:validatePassword - data:null");
            throw new SSOException("поле пароля не может быть пустым");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{5,15}$");
        Matcher match = pattern.matcher(password);
        if (!match.find()) {
            LOGGER.trace("UserValidatorService:validatePassword Пароль должен состоять из символов и цифр и иметь размер от 6 до 15 символов");
            throw new SSOException("Пароль должен состоять из символов и цифр и иметь размер от 6 до 15 символов");

        }
        return true;
    }
}
