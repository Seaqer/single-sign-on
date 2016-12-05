package service;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.RoleRepository;
import interfaces.service.Validator;
import models.RoleInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final Validator validator;
    private static final Logger LOGGER = Logger.getLogger(RoleService.class);


    @Autowired
    public RoleService(UserService userService, RoleRepository roleRepository, Validator validator) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.validator = validator;
    }

    public boolean createRole(String nameRole) throws SSOException {
        validator.validate(nameRole);

        Role role = new RoleInfo();
        role.setROLE_NAME(nameRole);
        roleRepository.addElement(role);
        return true;
    }

    public boolean updateRole(String nameRole, String newNameRole) throws SSOException {
        validator.validate(newNameRole);

        Role role = searchRole(nameRole);
        role.setROLE_NAME(newNameRole);
        if (roleRepository.updateElement(role) != 1) {
            LOGGER.trace("UserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return true;
    }

    public boolean deleteRole(String nameRole) throws SSOException {
        Role role = searchRole(nameRole);
        if (roleRepository.deleteElement(role) != 1) {
            LOGGER.trace("UserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return true;
    }

    public Role searchRole(String nameRole) throws SSOException {
        validator.validate(nameRole);

        Role role = new RoleInfo();
        role.setROLE_NAME(nameRole);

        List<Role> users = roleRepository.getElements(role);
        if (users.size() != 1) {
            LOGGER.trace("UserService:searchUser Роли не существует");
            throw new SSOException("Роли не существует");
        }
        return users.get(0);
    }

    public boolean giveUserRole(String login, String nameRole) throws SSOException {
        User user = userService.searchUser(login);
        Role role = searchRole(nameRole);
        roleRepository.giveUserRole(role, user);
        return true;
    }
}
