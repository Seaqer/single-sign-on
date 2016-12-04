package interfaces.repository;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import exceptions.SSOException;

public interface RoleRepository extends Repository<Role> {
    boolean giveUserRole(Role role, User user) throws SSOException;
}
