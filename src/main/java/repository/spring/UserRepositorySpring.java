package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.UserRepository;
import models.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserRepositorySpring implements UserRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Autowired
    public UserRepositorySpring(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userRowMapper = new UserRowMapper();
    }

    @Override
    public User addElement(User element) throws SSOException {
        String query = "INSERT INTO USERS(USER_ID, LOGIN, PASS) VALUES(USER_SEQ.NEXTVAL, :LOGIN , :PASS)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);

        namedParameterJdbcTemplate.update(query, sqlParameterSource, keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setUSER_ID(id);
        return element;
    }

    @Override
    public long updateElement(User element) throws SSOException {
        final String query = "UPDATE USERS SET LOGIN = :LOGIN , PASS = :PASS , DEL_USER = :DEL_USER where USER_ID = :USER_ID ;";
        return namedParameterJdbcTemplate.update(query, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public long deleteElement(User element) throws SSOException {
        final String query = "DELETE USERS WHERE USER_ID = :USER_ID";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);
        return namedParameterJdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public List<User> getElements(User element) throws SSOException {
        final String query = "SELECT USER_ID, LOGIN, PASS, DEL_USER FROM USERS WHERE LOGIN = :LOGIN ;";
        List<User> user = namedParameterJdbcTemplate.query(query, new MapSqlParameterSource("LOGIN", element.getLOGIN()), userRowMapper);
        if(user.isEmpty()){
            throw new SSOException("пользователь не найден");
        }
        return user;
    }

    @Override
    public int checkOperation(User user, Operation operation) throws SSOException {
        final String query = "SELECT  count(*) as count_operation" +
                "FROM USERS u, USER_ROLE ur, ROLE_OPERATION ro, OPERATIONS o " +
                "WHERE u.USER_ID = ur.USER_USER_ID " +
                "and ur.ROLE_ROLE_ID = ro.ROLE_ROLE_ID " +
                "and o.OPER_NAME = :OPER_NAME " +
                "and u.USER_ID = :USER_ID " +
                "and DEL_USER IS NULL;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("USER_ID", user.getUSER_ID());
        parameters.addValue("OPER_NAME", operation.getOPER_NAME());
        return namedParameterJdbcTemplate.queryForObject(query, parameters, Integer.class);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserInfo(
                    rs.getLong("USER_ID"),
                    rs.getString("LOGIN"),
                    rs.getString("PASS"),
                    rs.getLong("DEL_USER")
            );
        }
    }
}
