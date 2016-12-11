package interfaces.service;

import core.domain.authorization.Role;

/**
 * Сервисный слой для работы с ролями
 */
public interface RoleService {
    /**
     * Создание роли
     * @param nameRole имя роли
     * @return создана ли роль
     */
    boolean createRole(String nameRole);

    /**
     * Обновление роли
     * @param nameRole имя роли
     * @param newNameRole новое имя роли
     * @return обновлена ли роль
     */
     boolean updateRole(String nameRole, String newNameRole);

    /**
     * Удаление роли
     * @param nameRole имя роли
     * @return удалена ли роль
     */
     boolean deleteRole(String nameRole);

    /**
     * Поиск роли
     * @param nameRole имя роли
     * @return роль
     */
    public Role searchRole(String nameRole);

    /**
     * Выдача роли пользователю
     * @param login логин
     * @param nameRole имя роли
     * @return выдана ли роль
     */
    public boolean giveUserRole(String login, String nameRole);
}
