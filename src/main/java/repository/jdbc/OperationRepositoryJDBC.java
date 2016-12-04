package repository.jdbc;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import exceptions.SSOException;
import interfaces.repository.OperationRepository;
import models.OperationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OperationRepositoryJDBC implements OperationRepository {
    private final Connection connection;
    //final static Log LOGGER = LogFactory.getLog(RoleRepositoryJDBC.class);

    @Autowired
    public OperationRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Operation addElement(Operation element) throws SSOException {
        String query = "INSERT INTO OPERATIONS(OPER_ID , OPER_NAME) VALUES(OPER_SEQ.NEXTVAL, ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getOPER_NAME());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setOPER_ID(id);
                        return element;
                    } else {
                        //LOGGER.warn("Unable to get user id");
                        throw new SSOException("ошибка получения операции");
                    }
                }
            } else {
                //LOGGER.warn("User not created");
                throw new SSOException("операция не создана");
            }
        } catch (SQLException e) {
            //LOGGER.debug("OperationRepositoryJDBC:addElement",e);
            throw e.getErrorCode() == 23505 ? new SSOException("операция уже существует", e) : new SSOException("ошибка сервиса регистрации", e);
        }
    }

    @Override
    public long updateElement(Operation element) throws SSOException {
        final String query = "UPDATE OPERATIONS SET OPER_NAME= ? where OPER_ID = ?";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getOPER_NAME());
            statement.setLong(2, element.getOPER_ID());

            result = statement.executeUpdate();
        } catch (SQLException e) {
            //LOGGER.debug("OperationRepositoryJDBC:updateElement",e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public long deleteElement(Operation element) throws SSOException {
        final String query = "DELETE OPERATIONS WHERE OPER_ID = ?";
        long result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, element.getOPER_ID());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //LOGGER.debug("OperationRepositoryJDBC:deleteElement",e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public List<Operation> getElements(Operation element) throws SSOException {
        final String query = "SELECT OPER_ID , OPER_NAME FROM OPERATIONS WHERE OPER_NAME = ?;";
        final List<Operation> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getOPER_NAME());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Operation role = new OperationInfo();
                role.setOPER_ID(resultSet.getInt("OPER_ID"));
                role.setOPER_NAME(resultSet.getString("OPER_NAME"));
                users.add(role);
            }
        } catch (SQLException e) {
            //LOGGER.debug("OperationRepositoryJDBC:getElements",e);
            users.clear();
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return users;
    }

    @Override
    public boolean giveRoleOperation(Operation operation, Role role) throws SSOException {
        String query = "INSERT INTO ROLE_OPERATION( RLOP_ID , ROLE_ROLE_ID , OPER_OPER_ID ) VALUES(RLOP_SEQ.NEXTVAL, ? , ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, role.getROLE_ID());
            statement.setLong(1, operation.getOPER_ID());
            return  statement.executeUpdate() == 1;
        } catch (SQLException e) {
            //LOGGER.debug("OperationRepositoryJDBC:giveRoleOperation",e);
            throw e.getErrorCode() == 23505 ? new SSOException("роль уже ввыдана", e) : new SSOException("ошибка сервиса регистрации", e);
        }
    }
}
