package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.User;
import interfaces.repository.custom.UserRepository;
import domain.entity.InfoUser;
import domain.exceptions.SSOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для работы с пользователями в БД через Spring JDBC
 */
@Repository
public class SpringJdbcUserRepository implements UserRepository {
    private final String INSERT_USER = "INSERT INTO USERS(USER_ID, LOGIN, PASS) VALUES(USER_SEQ.NEXTVAL, :login , :password)";
    private final String UPDATE_USER = "UPDATE USERS SET LOGIN = :login , PASS = :password , DEL_USER = :delUser where USER_ID = :id ;";
    private final String DELETE_USER = "DELETE USERS WHERE USER_ID = :id";
    private final String SEARCH_USER = "SELECT USER_ID, LOGIN, PASS, DEL_USER FROM USERS WHERE LOGIN = :login ;";
    private final String USER_OPERATION = "SELECT  count(*) as count_operation FROM USERS u, USER_ROLE ur, ROLE_OPERATION ro, OPERATIONS o " +
            "WHERE u.USER_ID = ur.USER_USER_ID and ur.ROLE_ROLE_ID = ro.ROLE_ROLE_ID and o.OPER_NAME = :OPER_NAME " +
            "and u.USER_ID = :USER_ID and DEL_USER IS NULL;";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper userRowMapper;

    /**
     * Создать репозиторий
     *
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     */
    @Autowired
    public SpringJdbcUserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userRowMapper = new UserRowMapper();
    }

    @Override
    public User addElement(User element) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_USER, new BeanPropertySqlParameterSource(element), keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setId(id);
        return element;
    }

    @Override
    public long updateElement(User element) {
        return namedParameterJdbcTemplate.update(UPDATE_USER, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public long deleteElement(User element) {
        return namedParameterJdbcTemplate.update(DELETE_USER, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public List<User> getElements(User element) {
        List<User> user = namedParameterJdbcTemplate.query(SEARCH_USER, new MapSqlParameterSource("login", element.getLogin()), userRowMapper);
        if (user.isEmpty()) {
            throw new SSOException("пользователь не найден");
        }
        return user;
    }

    @Override
    public int checkOperation(User user, Operation operation) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("USER_ID", user.getId());
        parameters.addValue("OPER_NAME", operation.getName());
        return namedParameterJdbcTemplate.queryForObject(USER_OPERATION, parameters, Integer.class);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InfoUser(
                    rs.getLong("USER_ID"),
                    rs.getString("LOGIN"),
                    rs.getString("PASS"),
                    rs.getLong("DEL_USER")
            );
        }
    }
}
