package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import exceptions.SSOException;
import interfaces.repository.OperationRepository;

import java.util.List;

public class OperationRepositorySpring implements OperationRepository {
    @Override
    public Operation addElement(Operation element) throws SSOException {
        return null;
    }

    @Override
    public long updateElement(Operation element) throws SSOException {
        return 0;
    }

    @Override
    public long deleteElement(Operation element) throws SSOException {
        return 0;
    }

    @Override
    public List<Operation> getElements(Operation element) throws SSOException {
        return null;
    }

    @Override
    public boolean giveRoleOperation(Operation operation, Role role) throws SSOException {
        return false;
    }
}
