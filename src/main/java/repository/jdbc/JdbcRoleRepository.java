package repository.jdbc;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import domain.entity.InfoRole;
import domain.exceptions.SSOException;
import interfaces.repository.custom.RoleRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс для работы с ролями в БД через JDBC
 */
@Repository
public class JdbcRoleRepository implements RoleRepository {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(JdbcRoleRepository.class);

    /**
     * создать интерфейс
     * @param connection подключение к БД
     */
    @Autowired
    public JdbcRoleRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Role addElement(Role element) {

        String query = "INSERT INTO ROLES(ROLE_ID, ROLE_NAME) VALUES(ROLE_SEQ.NEXTVAL, ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getName());

            if (statement.executeUpdate() == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        element.setId(id);
                        return element;
                    } else {
                        LOGGER.trace("JdbcRoleRepository:addElement не удалось получить id");
                        throw new SSOException("ошибка получения роли");
                    }
                }
            } else {
                LOGGER.trace("JdbcRoleRepository:addElement роль не создана");
                throw new SSOException("роль не создана");
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("JdbcRoleRepository:addElement роль уже создана");
                throw new SSOException("роль уже существует", e);
            } else {
                LOGGER.error("JdbcRoleRepository:addElement", e);
                throw new SSOException(e);
            }
        }
    }

    @Override
    public long updateElement(Role element) {
        final String query = "UPDATE ROLES SET ROLE_NAME= ? where ROLE_ID = ?";
        long result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getName());
            statement.setLong(2, element.getId());

            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("JdbcRoleRepository:updateElement", e);
            throw new SSOException(e);
        }
        return result;
    }

    @Override
    public long deleteElement(Role element) {
        final String query = "DELETE ROLES WHERE ROLE_ID = ?";
        long result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, element.getId());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("JdbcRoleRepository:deleteElement", e);
            throw new SSOException(e);
        }
        return result;
    }

    @Override
    public List<Role> getElements(Role element) {
        final String query = "SELECT ROLE_ID , ROLE_NAME FROM ROLES WHERE ROLE_NAME = ?;";
        final List<Role> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, element.getName());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Role role = InfoRole.createEmpty();
                role.setId(resultSet.getLong("ROLE_ID"));
                role.setName(resultSet.getString("ROLE_NAME"));
                users.add(role);
            }
        } catch (SQLException e) {
            LOGGER.error("JdbcRoleRepository:getElements", e);
            users.clear();
            throw new SSOException(e);
        }
        return users;
    }

    @Override
    public boolean giveUserRole(Role role, User user) {
        String query = "INSERT INTO USER_ROLE( USRL_ID , ROLE_ROLE_ID , USER_USER_ID  ) VALUES(USRL_SEQ.NEXTVAL, ? , ? )";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, role.getId());
            statement.setLong(1, user.getId());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {

            if (e.getErrorCode() == 23505) {
                LOGGER.trace("JdbcRoleRepository:giveUserRole роль уже ввыдана");
                throw new SSOException("роль уже ввыдана", e);
            } else {
                LOGGER.error("JdbcRoleRepository:giveUserRole", e);
                throw new SSOException(e);
            }
        }
    }
}
