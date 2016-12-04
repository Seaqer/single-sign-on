package repository.jdbc;

import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;
import models.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserRepositoryJDBC implements UserRepository {
    private final Connection connection;
    //final static Log LOGGER = LogFactory.getLog(UserRepositoryJDBC.class);

    @Autowired
    public UserRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addElement(User element) throws SSOException {
        String query = "INSERT INTO USERS(USER_ID, LOGIN, PASS) VALUES(USER_SEQ.NEXTVAL, ? , ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLogin());
            statement.setString(2, element.getPasswd());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setUSER_ID(id);
                        return element;
                    } else {
                        //LOGGER.warn("Unable to get user id");
                        throw new SSOException("ошибка получения данных пользователя");
                    }
                }
            } else {
                //LOGGER.warn("User not created");
                throw new SSOException("пользователь не создан");
            }
        } catch (SQLException e) {
            //LOGGER.debug("UserRepositoryJDBC:addElement",e);
            throw e.getErrorCode() == 23505 ? new SSOException("логин уже существует", e) : new SSOException("ошибка сервиса регистрации", e);
        }
    }

    @Override
    public long updateElement(User element) throws SSOException {

        final String query = "UPDATE USERS SET LOGIN = ? , PASS = ? , DEL_USER = ? where USER_ID = ?;";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getLogin());
            statement.setString(2, element.getPasswd());
            statement.setLong(4, element.getUSER_ID());

            if (Objects.isNull(element.getDEL_USER())) {
                statement.setNull(3, 0);
            } else {
                statement.setLong(3, element.getDEL_USER());
            }

            result = statement.executeUpdate();
        } catch (SQLException e) {
            //LOGGER.debug("UserRepositoryJDBC:updateElement",e);
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
            //LOGGER.debug("UserRepositoryJDBC:deleteElement",e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public List<User> getElements(User element) throws SSOException {
        final String query = "SELECT USER_ID, LOGIN, PASS, DEL_USER FROM USERS WHERE LOGIN = ?;";
        final List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getLogin());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new UserInfo();
                user.setUSER_ID(resultSet.getInt("USER_ID"));
                user.setLogin(resultSet.getString("LOGIN"));
                user.setPasswd(resultSet.getString("PASS"));
                user.setDEL_USER(resultSet.getLong("DEL_USER"));
                users.add(user);
            }
        } catch (SQLException e) {
            //LOGGER.debug("UserRepositoryJDBC:getElements",e);
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
            //LOGGER.debug("UserRepositoryJDBC:checkOperation",e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
    }
}
