package interfaces.repository.custom;

import core.domain.authorization.Operation;
import core.domain.authorization.User;

/**
 * Репозиторий для пользователя
 */
public interface UserRepository extends Repository<User> {
    /**
     * Проверить доступна ли пользователю операция
     * @param user Пользователь - содержащий в себе имя
     * @param operation Операция - содержащая в себе имя
     * @return Количество найденых операций с именем для пользователя
     */
    int checkOperation(User user, Operation operation);
}
