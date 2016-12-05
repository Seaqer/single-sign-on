package repository.jdbc;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.RoleRepository;
import models.RoleInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoleRepositoryJDBC implements RoleRepository {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(RoleRepositoryJDBC.class);

    @Autowired
    public RoleRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Role addElement(Role element) throws SSOException {

        String query = "INSERT INTO ROLES(ROLE_ID, ROLE_NAME) VALUES(ROLE_SEQ.NEXTVAL, ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getROLE_NAME());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setROLE_ID(id);
                        return element;
                    } else {
                        LOGGER.trace("RoleRepositoryJDBC:addElement не удалось получить id");
                        throw new SSOException("ошибка получения роли");
                    }
                }
            } else {
                LOGGER.trace("RoleRepositoryJDBC:addElement роль не создана");
                throw new SSOException("роль не создана");
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("RoleRepositoryJDBC:addElement роль уже создана");
                throw new SSOException("роль уже существует", e);
            } else {
                LOGGER.error("RoleRepositoryJDBC:addElement", e);
                throw new SSOException("ошибка сервиса регистрации", e);
            }
        }
    }

    @Override
    public long updateElement(Role element) throws SSOException {
        final String query = "UPDATE ROLES SET ROLE_NAME= ? where ROLE_ID = ?";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getROLE_NAME());
            statement.setLong(2, element.getROLE_ID());

            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("RoleRepositoryJDBC:updateElement", e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public long deleteElement(Role element) throws SSOException {
        final String query = "DELETE ROLES WHERE ROLE_ID = ?";
        long result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, element.getROLE_ID());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("RoleRepositoryJDBC:deleteElement", e);
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return result;
    }

    @Override
    public List<Role> getElements(Role element) throws SSOException {
        final String query = "SELECT ROLE_ID , ROLE_NAME FROM ROLES WHERE ROLE_NAME = ?;";
        final List<Role> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getROLE_NAME());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Role role = new RoleInfo();
                role.setROLE_ID(resultSet.getInt("ROLE_ID"));
                role.setROLE_NAME(resultSet.getString("ROLE_NAME"));
                users.add(role);
            }
        } catch (SQLException e) {
            LOGGER.error("RoleRepositoryJDBC:getElements", e);
            users.clear();
            throw new SSOException("ошибка сервиса регистрации", e);
        }
        return users;
    }

    @Override
    public boolean giveUserRole(Role role, User user) throws SSOException {
        String query = "INSERT INTO USER_ROLE( USRL_ID , ROLE_ROLE_ID , USER_USER_ID  ) VALUES(USRL_SEQ.NEXTVAL, ? , ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, role.getROLE_ID());
            statement.setLong(1, user.getUSER_ID());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("RoleRepositoryJDBC:giveUserRole роль уже ввыдана");
                throw new SSOException("роль уже ввыдана", e);
            } else {
                LOGGER.error("RoleRepositoryJDBC:giveUserRole", e);
                throw new SSOException("ошибка сервиса регистрации", e);
            }
        }
    }
}
