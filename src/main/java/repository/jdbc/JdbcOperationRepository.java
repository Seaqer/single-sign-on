package repository.jdbc;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import domain.entity.InfoOperation;
import domain.exceptions.SSOException;
import interfaces.repository.custom.OperationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс для работы с операциями в БД через JDBC
 */
@Repository
public class JdbcOperationRepository implements OperationRepository {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(JdbcOperationRepository.class);

    /**
     * создать интерфейс
     * @param connection подключение к БД
     */
    @Autowired
    public JdbcOperationRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Operation addElement(Operation element) {
        String query = "INSERT INTO OPERATIONS(OPER_ID , OPER_NAME) VALUES(OPER_SEQ.NEXTVAL, ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getName());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setId(id);
                        return element;
                    } else {
                        LOGGER.trace("JdbcOperationRepository:addElement не удалось получить id");
                        throw new SSOException("ошибка получения операции");
                    }
                }
            } else {
                LOGGER.trace("JdbcOperationRepository:addElement операция не создана");
                throw new SSOException("операция не создана");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                LOGGER.debug("JdbcUserRepository:addElement операция уже существует");
                throw new SSOException("операция уже существует", e);
            } else {
                LOGGER.error("JdbcUserRepository:addElement", e);
                throw new SSOException(e);
            }
        }
    }

    @Override
    public long updateElement(Operation element) {
        final String query = "UPDATE OPERATIONS SET OPER_NAME= ? where OPER_ID = ?";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getName());
            statement.setLong(2, element.getId());

            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:updateElement", e);
            throw new SSOException(e);
        }
        return result;
    }

    @Override
    public long deleteElement(Operation element) throws SSOException {
        final String query = "DELETE OPERATIONS WHERE OPER_ID = ?";
        long result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, element.getId());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:deleteElement", e);
            throw new SSOException(e);
        }
        return result;
    }

    @Override
    public List<Operation> getElements(Operation element) {
        final String query = "SELECT OPER_ID , OPER_NAME FROM OPERATIONS WHERE OPER_NAME = ?;";
        final List<Operation> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getName());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Operation operation = InfoOperation.createEmpty();
                operation.setId(resultSet.getLong("OPER_ID"));
                operation.setName(resultSet.getString("OPER_NAME"));
                users.add(operation);
            }
        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:getElements", e);
            users.clear();
            throw new SSOException(e);
        }
        return users;
    }

    @Override
    public boolean giveRoleOperation(Operation operation, Role role) {
        String query = "INSERT INTO ROLE_OPERATION( RLOP_ID , ROLE_ROLE_ID , OPER_OPER_ID ) VALUES(RLOP_SEQ.NEXTVAL, ? , ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, role.getId());
            statement.setLong(1, operation.getId());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                LOGGER.trace("JdbcOperationRepository:giveRoleOperation роль уже ввыдана");
                throw new SSOException("роль уже ввыдана", e);
            } else {
                LOGGER.error("JdbcUserRepository:getElements", e);
                throw new SSOException(e);
            }
        }
    }
}
