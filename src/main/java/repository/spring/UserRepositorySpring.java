package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;

import java.util.List;

public class UserRepositorySpring implements UserRepository {
    @Override
    public User addElement(User element) throws SSOException {
        return null;
    }

    @Override
    public long updateElement(User element) throws SSOException {
        return 0;
    }

    @Override
    public long deleteElement(User element) throws SSOException {
        return 0;
    }

    @Override
    public List<User> getElements(User element) throws SSOException {
        return null;
    }

    @Override
    public int checkOperation(User user, Operation operation) throws SSOException {
        return 0;
    }
}
