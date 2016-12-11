package service;

import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import domain.entity.InfoOperation;
import domain.exceptions.SSOException;
import interfaces.repository.custom.OperationRepository;
import interfaces.service.OperationsService;
import interfaces.service.RoleService;
import interfaces.utils.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный слой для работы с операциями
 */
@Service
public class SimpleOperationsService implements OperationsService {
    private final OperationRepository operationRepository;
    private final RoleService roleService;
    private final Validator validator;
    private static final Logger LOGGER = Logger.getLogger(SimpleOperationsService.class);

    /**
     * Создать сервисный слой
     * @param roleService Сервис для работы с ролями
     * @param operationRepository Интерфейс для работы с БД
     * @param validator Интерфейс для проверки
     */
    @Autowired
    public SimpleOperationsService(RoleService roleService, OperationRepository operationRepository, Validator validator) {
        this.roleService = roleService;
        this.operationRepository = operationRepository;
        this.validator = validator;
    }

    /**
     * Создание операции
     * @param nameOperation Имя операции
     * @return создана ли операция
     */
    @Transactional(readOnly = true)
    public boolean createOperation(String nameOperation) {
        validator.validate(nameOperation);

        Operation role = InfoOperation.createEmpty();
        role.setName(nameOperation);
        operationRepository.addElement(role);
        return true;
    }

    /**
     * Обновление операции
     * @param nameOperation имя операции
     * @param newNameOperation новое имя операции
     * @return обновлена ли операция
     */
    @Transactional(readOnly = true)
    public boolean updateOperation(String nameOperation, String newNameOperation) {
        validator.validate(newNameOperation);

        Operation operation = searchOperation(nameOperation);
        operation.setName(newNameOperation);
        if(operationRepository.updateElement(operation) != 1) {
            LOGGER.trace("SimpleOperationsService:updateOperation Операции не существует");
            throw new SSOException("Операции не существует");
        }
        return true;
    }

    /**
     * Удаление операции
     * @param nameOperation имя операции
     * @return удалена ли операция
     */
    @Transactional(readOnly = true)
    public boolean deleteOperation(String nameOperation) {
        Operation operation = searchOperation(nameOperation);
        if(operationRepository.deleteElement(operation) != 1) {
            LOGGER.trace("SimpleOperationsService:deleteOperation Операции не существует");
            throw new SSOException("Операциии не существует");
        }
        return true;
    }

    /**
     * Поиск операции
     * @param nameOperation имя операции
     * @return Операция
     */
    @Transactional(readOnly = true)
    public Operation searchOperation(String nameOperation) {
        validator.validate(nameOperation);

        Operation operation = InfoOperation.createEmpty();
        operation.setName(nameOperation);

        List<Operation> users = operationRepository.getElements(operation);
        if (users.size() != 1) {
            LOGGER.trace("SimpleOperationsService:searchUser Операции не существует");
            throw new SSOException("Операции не существует");
        }
        return users.get(0);
    }

    /**
     * Выдать операцию роли
     * @param nameOperation имя операции
     * @param nameRole имя роли
     * @return выдана ли операция
     */
    @Transactional(readOnly = true)
    public boolean giveRoleOperation(String nameOperation, String nameRole) {
        validator.validate(nameOperation);
        validator.validate(nameRole);

        Role role = roleService.searchRole(nameRole);
        Operation operation = searchOperation(nameOperation);
        operationRepository.giveRoleOperation(operation, role);
        return true;
    }

}
