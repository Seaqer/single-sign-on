package service.support;

import exceptions.SSOException;
import interfaces.service.UserValidator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidatorService implements UserValidator {
    //final static Log LOGGER = LogFactory.getLog(UserValidatorService.class);

    @Override
    public boolean validate(String login, String password) throws SSOException {
        return validateLogin(login) && validatePassword(password);
    }

    @Override
    public boolean validateLogin(String login) throws SSOException {
        if (Objects.isNull(login)) {
            //LOGGER.debug(e);
            throw new SSOException("поле логина не может быть пустым");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{4,20}$");
        Matcher match = pattern.matcher(login);
        if (!match.find()) {
            //LOGGER.debug(e);
            throw new SSOException("Логин должен состоять из символов и цифр, начинаться с буквы и иметь длинну от 5 до 20 символов");
        }
        return true;
    }

    @Override
    public boolean validatePassword(String password) throws SSOException {
        if (Objects.isNull(password)) {
            //LOGGER.debug(e);
            throw new SSOException("поле пароля не может быть пустым");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{5,15}$");
        Matcher match = pattern.matcher(password);
        if (!match.find()) {
            //LOGGER.debug(e);
            throw new SSOException("Пароль должен состоять из символов и цифр и иметь размер от 6 до 15 символов");

        }
        return true;
    }
}