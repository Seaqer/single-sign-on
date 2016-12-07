package repository.spring;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import exceptions.SSOException;
import interfaces.repository.RoleRepository;
import models.RoleInfo;
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
public class RoleRepositorySpring implements RoleRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RoleRowMapper roleRowMapper;

    @Autowired
    public RoleRepositorySpring(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.roleRowMapper = new RoleRowMapper();
    }

    @Override
    public Role addElement(Role element) throws SSOException {
        String query = "INSERT INTO ROLES(ROLE_ID, ROLE_NAME) VALUES(ROLE_SEQ.NEXTVAL, :ROLE_NAME )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);

        namedParameterJdbcTemplate.update(query, sqlParameterSource, keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setROLE_ID(id);
        return element;
    }

    @Override
    public long updateElement(Role element) throws SSOException {
        final String query = "UPDATE ROLES SET ROLE_NAME= :ROLE_NAME where ROLE_ID = :ROLE_ID";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);
        return namedParameterJdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public long deleteElement(Role element) throws SSOException {
        final String query = "DELETE ROLES WHERE ROLE_ID = :ROLE_ID";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);
        return namedParameterJdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public List<Role> getElements(Role element) throws SSOException {
        final String query = "SELECT ROLE_ID , ROLE_NAME FROM ROLES WHERE ROLE_NAME = :ROLE_NAME;";
        List<Role> roles = namedParameterJdbcTemplate.query(query, new MapSqlParameterSource("ROLE_NAME", element.getROLE_NAME()), roleRowMapper);
        if(roles.isEmpty()){
            throw new SSOException("не найдено ролей");
        }
        return roles;
    }

    @Override
    public boolean giveUserRole(Role role, User user) throws SSOException {
        String query = "INSERT INTO USER_ROLE( USRL_ID , ROLE_ROLE_ID , USER_USER_ID  ) VALUES(USRL_SEQ.NEXTVAL, :ROLE_ID , :USER_ID )";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ROLE_ID",role.getROLE_ID());
        parameters.addValue("USER_ID",user.getUSER_ID());

        namedParameterJdbcTemplate.update(query,parameters);
        return true;
    }

    private static class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RoleInfo(
                    rs.getLong("ROLE_ID"),
                    rs.getString("ROLE_NAME")
            );
        }
    }
}
