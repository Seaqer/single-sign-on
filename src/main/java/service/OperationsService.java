package service;

import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import exceptions.SSOException;
import interfaces.repository.OperationRepository;
import interfaces.service.Validator;
import models.OperationInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationsService {
    private final OperationRepository operationRepository;
    private final RoleService roleService;
    private final Validator validator;
    private static final Logger LOGGER = Logger.getLogger(OperationsService.class);

    @Autowired
    public OperationsService(RoleService roleService, OperationRepository operationRepository, Validator validator) {
        this.roleService = roleService;
        this.operationRepository = operationRepository;
        this.validator = validator;
    }

    public boolean createOperation(String nameOperation) throws SSOException {
        validator.validate(nameOperation);

        Operation role = new OperationInfo();
        role.setOPER_NAME(nameOperation);
        operationRepository.addElement(role);
        return true;
    }

    public boolean updateOperation(String nameOperation, String newNameOperation) throws SSOException {
        validator.validate(newNameOperation);

        Operation operation = searchOperation(nameOperation);
        operation.setOPER_NAME(newNameOperation);
        if(operationRepository.updateElement(operation) != 1) {
            LOGGER.trace("OperationsService:updateOperation Операции не существует");
            throw new SSOException("Операции не существует");
        }
        return true;
    }

    public boolean deleteOperation(String nameOperation) throws SSOException {
        Operation operation = searchOperation(nameOperation);
        if(operationRepository.deleteElement(operation) != 1) {
            LOGGER.trace("OperationsService:deleteOperation Операции не существует");
            throw new SSOException("Операциии не существует");
        }
        return true;
    }

    public Operation searchOperation(String nameOperation) throws SSOException {
        validator.validate(nameOperation);

        Operation operation = new OperationInfo();
        operation.setOPER_NAME(nameOperation);

        List<Operation> users = operationRepository.getElements(operation);
        if (users.size() != 1) {
            LOGGER.trace("OperationsService:searchUser Операции не существует");
            throw new SSOException("Операции не существует");
        }
        return users.get(0);
    }

    boolean giveRoleOperation(String nameOperation, String nameRole) throws SSOException {
        validator.validate(nameOperation);

        Role role = roleService.searchRole(nameRole);
        Operation operation = searchOperation(nameOperation);
        operationRepository.giveRoleOperation(operation, role);
        return true;
    }

}
