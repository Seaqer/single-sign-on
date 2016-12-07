package service;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;
import interfaces.service.PasswordManager;
import interfaces.service.UserValidator;
import models.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final PasswordManager passwordManager;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private static final Logger LOGGER = Logger.getLogger(UserService.class);


    @Autowired
    public UserService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        this.passwordManager = passwordManager;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public boolean registrationUser(String login, String password) throws SSOException {
        userValidator.validate(login, password);

        User user = new UserInfo();
        user.setLOGIN(login);
        user.setPASS(passwordManager.getPassword(password));
        userRepository.addElement(user);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean updateLogin(String login, String newLogin) throws SSOException {
        userValidator.validateLogin(login);
        userValidator.validateLogin(newLogin);

        User user = searchUser(login);
        user.setLOGIN(newLogin);
        if (userRepository.updateElement(user) != 1) {
            LOGGER.trace("UserService:updateLogin Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean updatePassword(String login, String newPassword) throws SSOException {
        userValidator.validate(login, newPassword);

        User user = searchUser(login);
        user.setPASS(passwordManager.getPassword(newPassword));
        if (userRepository.updateElement(user) != 1) {
            LOGGER.trace("UserService:closeUser Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean openUser(String login) throws SSOException {
        User user = searchUser(login);
        if (Objects.isNull(user.getDEL_USER())) {
            LOGGER.trace("UserService:closeUser Пользователя не заблокирован");
            throw new SSOException("Пользователь не заблокирован");
        } else {
            user.setDEL_USER(null);
            if (userRepository.updateElement(user) != 1) {
                LOGGER.trace("UserService:closeUser Пользователя не существует");
                throw new SSOException("Пользователя не существует");
            }

        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean closeUser(String login, String del_user_login) throws SSOException {
        User user = searchUser(login);

        if (!Objects.isNull(user.getDEL_USER())) {
            LOGGER.trace("UserService:closeUser Пользователя уже заблокирован");
            throw new SSOException("Пользователь уже заблокирован");
        } else {
            User del_user = searchUser(del_user_login);
            user.setDEL_USER(del_user.getUSER_ID());
            if (userRepository.updateElement(user) != 1) {
                LOGGER.trace("UserService:closeUser Пользователя не существует");
                throw new SSOException("Пользователя не существует");
            }
        }
        return true;
    }
    @Transactional(readOnly = true)
    public User searchUser(String login) throws SSOException {
        userValidator.validateLogin(login);

        User user = new UserInfo();
        user.setLOGIN(login);

        List<User> users = userRepository.getElements(user);
        if (users.size() != 1) {
            LOGGER.trace("UserService:searchUser Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }
        return users.get(0);
    }

    @Transactional(readOnly = true)
    public boolean hasPremission(User user, Operation operation) throws SSOException {
        switch (userRepository.checkOperation(user, operation)) {
            case 1:
                return true;
            case 0:
                return false;
            default:
                LOGGER.trace("UserService:updateLogin invalid number of operations");
                throw new SSOException("UserService:hasPremission() - invalid number of operations");

        }
    }
}
