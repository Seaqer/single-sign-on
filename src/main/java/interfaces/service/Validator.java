package interfaces.service;


import exceptions.SSOException;

public interface Validator {
    boolean validate(String data) throws SSOException;
}
