package service.support;

import exceptions.SSOException;
import interfaces.service.PasswordManager;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashPasswordManager implements PasswordManager {
    //final Log LOGGER = LogFactory.getLog(HashPasswordManager.class);

    public String getPassword(String data) throws SSOException {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            result = new String(messageDigest.digest(data.getBytes()), "UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            //LOGGER.debug(e);
            throw new SSOException("failed getting hash", e);
        }
        return result;
    }
}
