package service;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import domain.entity.InfoUser;
import domain.exceptions.SSOException;
import interfaces.repository.custom.UserRepository;
import interfaces.service.UserService;
import interfaces.utils.PasswordManager;
import interfaces.utils.UserValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Сервисный слой для работы с пользователями
 */
@Service
public class SimpleUserService implements UserService {
    private final PasswordManager passwordManager;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private static final Logger LOGGER = Logger.getLogger(SimpleUserService.class);

    /**
     * Создать сервисный слой
     * @param passwordManager Менеджер паролей
     * @param userValidator Интерфейс для проверки пользователей
     * @param userRepository Интерфейс для работы с БД
     */
    @Autowired
    public SimpleUserService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        this.passwordManager = passwordManager;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    /**
     * Регистрация пользователя
     * @param login логин
     * @param password пароль
     * @return зарегистрирован ли пользователь
     */
    @Transactional(readOnly = true)
    public boolean registrationUser(String login, String password) {
        userValidator.validate(login, password);
        User user = InfoUser.createEmpty();
        user.setLogin(login);
        user.setPassword(passwordManager.getPassword(password));
        userRepository.addElement(user);
        return true;
    }

    /**
     * Сменить логин пользователя
     * @param login логин
     * @param newLogin новый логин
     * @return зарегистрирован ли пользователь
     */
    @Transactional(readOnly = true)
    public boolean updateLogin(String login, String newLogin) {
        userValidator.validateLogin(login);
        userValidator.validateLogin(newLogin);

        User user = searchUser(login);
        user.setLogin(newLogin);
        if (userRepository.updateElement(user) != 1) {
            LOGGER.trace("SimpleUserService:updateLogin Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }

        return true;
    }

    /**
     * Сменить пароль пользователя
     * @param login логин
     * @param newPassword новый пароль
     * @return сменен ли пароль
     */
    @Transactional(readOnly = true)
    public boolean updatePassword(String login, String newPassword) {
        userValidator.validate(login, newPassword);

        User user = searchUser(login);
        user.setPassword(passwordManager.getPassword(newPassword));
        if (userRepository.updateElement(user) != 1) {
            LOGGER.trace("SimpleUserService:closeUser Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }
        return true;
    }

    /**
     * Разблокировать пользователя
     * @param login логин
     * @return разблокирован ли прользователь
     */
    @Transactional(readOnly = true)
    public boolean openUser(String login) {
        User user = searchUser(login);
        if (Objects.isNull(user.getDelUser())) {
            LOGGER.trace("SimpleUserService:closeUser Пользователя не заблокирован");
            throw new SSOException("Пользователь не заблокирован");
        } else {
            user.setDelUser(null);
            if (userRepository.updateElement(user) != 1) {
                LOGGER.trace("SimpleUserService:closeUser Пользователя не существует");
                throw new SSOException("Пользователя не существует");
            }

        }
        return true;
    }

    /**
     * Закрыть пользователя
     * @param login логин закрываемого пользователя
     * @param delUserLogin логин закрывающего пользователя
     * @return закрыт ли пользователь
     */
    @Transactional(readOnly = true)
    public boolean closeUser(String login, String delUserLogin) {
        User user = searchUser(login);

        if (!Objects.isNull(user.getDelUser())) {
            LOGGER.trace("SimpleUserService:closeUser Пользователя уже заблокирован");
            throw new SSOException("Пользователь уже заблокирован");
        } else {
            User del_user = searchUser(delUserLogin);
            user.setDelUser(del_user.getId());
            if (userRepository.updateElement(user) != 1) {
                LOGGER.trace("SimpleUserService:closeUser Пользователя не существует");
                throw new SSOException("Пользователя не существует");
            }
        }
        return true;
    }

    /**
     * Найти пользователя
     * @param login логин
     * @return Найденный пользователь
     */
    @Transactional(readOnly = true)
    public User searchUser(String login) {
        userValidator.validateLogin(login);

        User user = InfoUser.createEmpty();
        user.setLogin(login);

        List<User> users = userRepository.getElements(user);
        if (users.size() != 1) {
            LOGGER.trace("SimpleUserService:searchUser Пользователя не существует");
            throw new SSOException("Пользователя не существует");
        }
        return users.get(0);
    }

    /**
     * Проверка прав у пользователя на операцию
     * @param user Пользователь
     * @param operation Операция
     * @return есть ли права на операцию
     */
    @Transactional(readOnly = true)
    public boolean hasPremission(User user, Operation operation) {
        switch (userRepository.checkOperation(user, operation)) {
            case 1:
                return true;
            case 0:
                return false;
            default:
                LOGGER.trace("SimpleUserService:updateLogin invalid number of operations");
                throw new SSOException("SimpleUserService:hasPremission() - invalid number of operations");

        }
    }

    //Отобрать права
    //
}
