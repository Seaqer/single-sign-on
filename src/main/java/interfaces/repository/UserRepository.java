package interfaces.repository;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;

public interface UserRepository extends Repository<User> {
    int checkOperation(User user, Operation operation) throws SSOException;
}
