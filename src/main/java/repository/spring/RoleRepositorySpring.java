package repository.spring;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.RoleRepository;

import java.util.List;

public class RoleRepositorySpring implements RoleRepository {
    @Override
    public Role addElement(Role element) throws SSOException {
        return null;
    }

    @Override
    public long updateElement(Role element) throws SSOException {
        return 0;
    }

    @Override
    public long deleteElement(Role element) throws SSOException {
        return 0;
    }

    @Override
    public List<Role> getElements(Role element) throws SSOException {
        return null;
    }

    @Override
    public boolean giveUserRole(Role role, User user) throws SSOException {
        return false;
    }
}
