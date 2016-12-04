package interfaces.service;


import exceptions.SSOException;

public interface UserValidator {
    boolean validate(String login, String password) throws SSOException;

    boolean validateLogin(String login) throws SSOException;

    boolean validatePassword(String password) throws SSOException;
}
