package repository.jdbc;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;
import models.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserRepositoryJDBC implements UserRepository {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(UserRepositoryJDBC.class);

    @Autowired
    public UserRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addElement(User element) throws SSOException {
        String query = "INSERT INTO USERS(USER_ID, LOGIN, PASS) VALUES(USER_SEQ.NEXTVAL, ? , ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLOGIN());
            statement.setString(2, element.getPASS());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setUSER_ID(id);
                        return element;
                    } else {
                        LOGGER.trace("UserRepositoryJDBC:addElement не удалось получить id");
                        throw new SSOException("ошибка получения данных пользователя");
                    }
                }
            } else {
                LOGGER.trace("UserRepositoryJDBC:addElement пользователь не создан");
                throw new SSOException("пользователь не создан");
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("логин уже существует",e);
                throw new SSOException("логин уже существует", e);
            } else {
                LOGGER.error("UserRepositoryJDBC:addElement", e);
                throw new SSOException("ошибка сервиса регистрации", e);
            }
        }
    }

    @Override
    public long updateElement(User element) throws SSOException {

        final String query = "UPDATE USERS SET LOGIN = ? , PASS = ? , DEL_USER = ? where USER_ID = ?;";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLOGIN());
            statement.setString(2, element.getPASS());
            statement.setLong(4, element.getUSER_ID());

            if (Objects.isNull(element.getDEL_USER())) {
                statement.setNull(3, 0);
            } else {
                statement.setLong(3, element.getDEL_USER());
            }

            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("UserRepositoryJDBC:updateElement", e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public long deleteElement(User element) throws SSOException {
        final String query = "DELETE USERS WHERE USER_ID = ?";
        long result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, element.getUSER_ID());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("UserRepositoryJDBC:deleteElement", e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public List<User> getElements(User element) throws SSOException {
        final String query = "SELECT USER_ID, LOGIN, PASS, DEL_USER FROM USERS WHERE LOGIN = ?;";
        final List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getLOGIN());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new UserInfo();
                user.setUSER_ID(resultSet.getInt("USER_ID"));
                user.setLOGIN(resultSet.getString("LOGIN"));
                user.setPASS(resultSet.getString("PASS"));
                user.setDEL_USER(resultSet.getLong("DEL_USER"));
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.error("UserRepositoryJDBC:getElements", e);
            users.clear();
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return users;
    }

    @Override
    public int checkOperation(User user, Operation operation) throws SSOException {
        final String query = "SELECT  count(*) as count_operation" +
                "FROM USERS u, USER_ROLE ur, ROLE_OPERATION ro, OPERATIONS o " +
                "WHERE u.USER_ID = ur.USER_USER_ID " +
                "and ur.ROLE_ROLE_ID = ro.ROLE_ROLE_ID " +
                "and o.OPER_NAME = ? " +
                "and u.USER_ID = ? " +
                "and DEL_USER IS NULL;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, operation.getOPER_NAME());
            statement.setLong(2, user.getUSER_ID());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("count_operation");

        } catch (SQLException e) {
            LOGGER.error("UserRepositoryJDBC:checkOperation", e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
    }
}
