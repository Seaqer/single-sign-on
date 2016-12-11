package interfaces.service;

import core.domain.authorization.Operation;

/**
 * Сервисный слой для работы с операциями
 */
public interface OperationsService {

    /**
     * Создание операции
     * @param nameOperation Имя операции
     * @return создана ли операция
     */
    boolean createOperation(String nameOperation);

    /**
     * Обновление операции
     * @param nameOperation имя операции
     * @param newNameOperation новое имя операции
     * @return обновлена ли операция
     */
    boolean updateOperation(String nameOperation, String newNameOperation);

    /**
     * Удаление операции
     * @param nameOperation имя операции
     * @return удалена ли операция
     */
    boolean deleteOperation(String nameOperation);

    /**
     * Поиск операции
     * @param nameOperation имя операции
     * @return Операция
     */
    Operation searchOperation(String nameOperation);

    /**
     * Выдать операцию роли
     * @param nameOperation имя операции
     * @param nameRole имя роли
     * @return выдана ли операция
     */
    boolean giveRoleOperation(String nameOperation, String nameRole);
}
