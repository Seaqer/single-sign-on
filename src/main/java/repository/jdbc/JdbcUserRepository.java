package repository.jdbc;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import domain.entity.InfoUser;
import domain.exceptions.SSOException;
import interfaces.repository.custom.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Интерфейс для работы с пользователями в БД через JDBC
 */
@Repository
public class JdbcUserRepository implements UserRepository {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(JdbcUserRepository.class);

    /**
     * создать интерфейс
     * @param connection подключение к БД
     */
    @Autowired
    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addElement(User element) {
        String query = "INSERT INTO USERS(USER_ID, LOGIN, PASS) VALUES(USER_SEQ.NEXTVAL, ? , ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLogin());
            statement.setString(2, element.getPassword());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setId(id);
                        return element;
                    } else {
                        LOGGER.trace("JdbcUserRepository:addElement не удалось получить id");
                        throw new SSOException("ошибка получения данных пользователя");
                    }
                }
            } else {
                LOGGER.trace("JdbcUserRepository:addElement пользователь не создан");
                throw new SSOException("пользователь не создан");
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("логин уже существует",e);
                throw new SSOException("логин уже существует", e);
            } else {
                LOGGER.error("JdbcUserRepository:addElement", e);
                throw new SSOException(e);
            }
        }
    }

    @Override
    public long updateElement(User element) {

        final String query = "UPDATE USERS SET LOGIN = ? , PASS = ? , DEL_USER = ? where USER_ID = ?;";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLogin());
            statement.setString(2, element.getPassword());
            statement.setLong(4, element.getId());

            if (Objects.isNull(element.getDelUser())) {
                statement.setNull(3, 0);
            } else {
                statement.setLong(3, element.getDelUser());
            }

            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:updateElement", e);
            throw new SSOException(e);
        }
        return result;
    }

    @Override
    public long deleteElement(User element) {
        final String query = "DELETE USERS WHERE USER_ID = ?";
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
    public List<User> getElements(User element) {
        final String query = "SELECT USER_ID, LOGIN, PASS, DEL_USER FROM USERS WHERE LOGIN = ?;";
        final List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getLogin());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = InfoUser.createEmpty();
                user.setId(resultSet.getLong("USER_ID"));
                user.setLogin(resultSet.getString("LOGIN"));
                user.setPassword(resultSet.getString("PASS"));
                user.setDelUser(resultSet.getLong("DEL_USER"));
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:getElements", e);
            users.clear();
            throw new SSOException(e);
        }
        return users;
    }

    @Override
    public int checkOperation(User user, Operation operation) {
        final String query = "SELECT  count(*) as count_operation" +
                "FROM USERS u, USER_ROLE ur, ROLE_OPERATION ro, OPERATIONS o " +
                "WHERE u.USER_ID = ur.USER_USER_ID " +
                "and ur.ROLE_ROLE_ID = ro.ROLE_ROLE_ID " +
                "and o.OPER_NAME = ? " +
                "and u.USER_ID = ? " +
                "and DEL_USER IS NULL;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, operation.getName());
            statement.setLong(2, user.getId());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("count_operation");

        } catch (SQLException e) {
            LOGGER.error("JdbcUserRepository:checkOperation", e);
            throw new SSOException(e);
        }
    }
}
