package interfaces.repository;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import exceptions.SSOException;

public interface OperationRepository extends Repository<Operation> {
    boolean giveRoleOperation(Operation operation, Role role) throws SSOException;
}
