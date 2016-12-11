package interfaces.repository.custom;


import core.domain.authorization.Role;
import core.domain.authorization.User;

/**
 * Репозиторий для роли
 */
public interface RoleRepository extends Repository<Role> {

    /**
     * Выдать роль пользователю
     * @param role Роль - содержащая в себе имя
     * @param user Пользователь - содержащий в себе имя
     * @return Выдана ли роль на пользователю
     */
    boolean giveUserRole(Role role, User user);
}
