package interfaces.repository.custom;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;

/**
 * Интерфейс для доступа к БД
 */
public interface OperationRepository extends Repository<Operation> {
    /**
     * Выдать разрешение на операцию для роли
     * @param operation Операция - содержащая в себе имя
     * @param role Роль - содержащая в себе имя
     * @return Выданы ли права на операцию
     */
    boolean giveRoleOperation(Operation operation, Role role);
}
