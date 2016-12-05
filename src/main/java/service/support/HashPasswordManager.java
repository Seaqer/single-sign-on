package service.support;

import exceptions.SSOException;
import interfaces.service.PasswordManager;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashPasswordManager implements PasswordManager {
    private static final Logger LOGGER = Logger.getLogger(HashPasswordManager.class);

    public String getPassword(String data) throws SSOException {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            result = new String(messageDigest.digest(data.getBytes()), "UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error("failed getting hash", e);
            throw new SSOException("failed getting hash", e);
        }
        return result;
    }
}
