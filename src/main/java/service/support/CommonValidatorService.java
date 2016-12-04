package service.support;


import exceptions.SSOException;
import interfaces.service.Validator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidatorService implements Validator {

    public boolean validate(String data) throws SSOException {
        if (Objects.isNull(data)) {
            //LOGGER.debug("CommonValidatorService:validate - data:null ");
            throw new SSOException("поле логина не может быть пустым");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{3,25}$");
        Matcher match = pattern.matcher(data);
        if (!match.find()) {
            //LOGGER.debug("CommonValidatorService:validate - failed");
            throw new SSOException("должен состоять из символов и цифр, начинаться с буквы и иметь длинну от 3 до 25 символов");
        }
        return true;
    }

}