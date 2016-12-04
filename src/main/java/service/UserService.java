package service;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;
import interfaces.service.PasswordManager;
import interfaces.service.UserValidator;
import models.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

@Component
public class UserService {
    private final PasswordManager passwordManager;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    //final static Log LOGGER = LogFactory.getLog(UserService.class);

    @Autowired
    public UserService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        this.passwordManager = passwordManager;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    public boolean registrationUser(String login, String password) throws SSOException {
        userValidator.validate(login, password);

        User user = new UserInfo();
        user.setLogin(login);
        user.setPasswd(passwordManager.getPassword(password));
        userRepository.addElement(user);
        return true;
    }

    public boolean updateLogin(String login, String newLogin) throws SSOException {
        User user = searchUser(login);
        user.setLogin(newLogin);
        if (userRepository.updateElement(user) != 1) {
            throw new SSOException("Пользователя не существует");
        }
        return true;
    }

    public boolean updatePassword(String login, String newPassword) throws SSOException {
        userValidator.validatePassword(newPassword);

        User user = searchUser(login);
        user.setPasswd(passwordManager.getPassword(newPassword));
        if (userRepository.updateElement(user) != 1) {
            throw new SSOException("Пользователя не существует");
        }
        return true;
    }

    public boolean openUser(String login) throws SSOException {
        User user = searchUser(login);
        if (Objects.isNull(user.getDEL_USER())) {
            throw new SSOException("Пользователь не заблокирован");
        } else {
            user.setDEL_USER(null);
            if (userRepository.updateElement(user) != 1) {
                throw new SSOException("Пользователя не существует");
            }

        }
        return true;
    }

    public boolean closeUser(String login, String del_user_login) throws SSOException {
        User user = searchUser(login);

        if (!Objects.isNull(user.getDEL_USER())){
            throw new SSOException("Пользователь уже заблокирован");
        }else {
            User del_user = searchUser(del_user_login);
            user.setDEL_USER(del_user.getUSER_ID());
            if (userRepository.updateElement(user) != 1) {
                throw new SSOException("Пользователя не существует");
            }
        }
        return true;
    }

    public User searchUser(String login) throws SSOException {
        userValidator.validateLogin(login);

        User user = new UserInfo();
        user.setLogin(login);

        List<User> users = userRepository.getElements(user);
        if (users.size() != 1) {
            throw new SSOException("Пользователя не существует");
        }
        return users.get(0);
    }

    public boolean hasPremission(User user, Operation operation) throws SSOException {
        switch (userRepository.checkOperation(user, operation)) {
            case 1:
                return true;
            case 0:
                return false;
            default:
                throw new SSOException("UserService:hasPremission() - invalid number of operations");

        }
    }
}
