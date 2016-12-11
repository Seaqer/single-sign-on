package service;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import domain.entity.InfoRole;
import domain.exceptions.SSOException;
import interfaces.repository.custom.RoleRepository;
import interfaces.service.RoleService;
import interfaces.service.UserService;
import interfaces.utils.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный слой для работы с ролями
 */
@Service
public class SimpleRoleService implements RoleService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final Validator validator;
    private static final Logger LOGGER = Logger.getLogger(SimpleRoleService.class);

    /**
     * Создать сервисный слой
     * @param userService Сервис для работы с пользователями
     * @param roleRepository Интерфейс для работы с БД
     * @param validator Интерфейс для проверки
     */
    @Autowired
    public SimpleRoleService(UserService userService, RoleRepository roleRepository, Validator validator) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.validator = validator;
    }

    /**
     * Создание роли
     * @param nameRole имя роли
     * @return создана ли роль
     */
    @Transactional(readOnly = true)
    public boolean createRole(String nameRole) {
        validator.validate(nameRole);

        Role role = InfoRole.createEmpty();
        role.setName(nameRole);
        roleRepository.addElement(role);
        return true;
    }

    /**
     * Обновление роли
     * @param nameRole имя роли
     * @param newNameRole новое имя роли
     * @return обновлена ли роль
     */
    @Transactional(readOnly = true)
    public boolean updateRole(String nameRole, String newNameRole) {
        validator.validate(newNameRole);

        Role role = searchRole(nameRole);
        role.setName(newNameRole);
        if (roleRepository.updateElement(role) != 1) {
            LOGGER.trace("SimpleUserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return true;
    }

    /**
     * Удаление роли
     * @param nameRole имя роли
     * @return удалена ли роль
     */
    @Transactional(readOnly = true)
    public boolean deleteRole(String nameRole) {
        Role role = searchRole(nameRole);
        if (roleRepository.deleteElement(role) != 1) {
            LOGGER.trace("SimpleUserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return true;
    }

    /**
     * Поиск роли
     * @param nameRole имя роли
     * @return роль
     */
    @Transactional(readOnly = true)
    public Role searchRole(String nameRole) {
        validator.validate(nameRole);

        Role role = InfoRole.createEmpty();
        role.setName(nameRole);

        List<Role> users = roleRepository.getElements(role);
        if (users.size() != 1) {
            LOGGER.trace("SimpleUserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return users.get(0);
    }

    /**
     * Выдача роли пользователю
     * @param login логин
     * @param nameRole имя роли
     * @return выдана ли роль
     */
    @Transactional(readOnly = true)
    public boolean giveUserRole(String login, String nameRole) {
        User user = userService.searchUser(login);
        Role role = searchRole(nameRole);
        roleRepository.giveUserRole(role, user);
        return true;
    }
}
