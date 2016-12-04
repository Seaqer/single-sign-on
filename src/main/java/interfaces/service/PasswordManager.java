package interfaces.service;


import exceptions.SSOException;

public interface PasswordManager {
    String getPassword(String data) throws SSOException;
}
