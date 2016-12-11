package repository.spring;


import core.domain.authorization.Role;
import core.domain.authorization.User;
import interfaces.repository.custom.RoleRepository;
import domain.entity.InfoRole;
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
 * Интерфейс для работы с ролями в БД через Spring JDBC
 */
@Repository
public class SpringJdbcRoleRepository implements RoleRepository {
    private final String INSERT_ROLE = "INSERT INTO ROLES(ROLE_ID, ROLE_NAME) VALUES(ROLE_SEQ.NEXTVAL, :name )";
    private final String UPDATE_ROLE = "UPDATE ROLES SET ROLE_NAME= :name where ROLE_ID = :id";
    private final String DELETE_ROLE = "DELETE ROLES WHERE ROLE_ID = :id";
    private final String SEARCH_ROLE = "SELECT ROLE_ID , ROLE_NAME FROM ROLES WHERE ROLE_NAME = :name;";
    private final String GIVE_USER_ROLE = "INSERT INTO USER_ROLE( USRL_ID , ROLE_ROLE_ID , USER_USER_ID  ) VALUES(USRL_SEQ.NEXTVAL, :id_role , :id_user )";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RoleRowMapper roleRowMapper;

    /**
     * Создать репозиторий
     *
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     */
    @Autowired
    public SpringJdbcRoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.roleRowMapper = new RoleRowMapper();
    }

    @Override
    public Role addElement(Role element) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_ROLE, new BeanPropertySqlParameterSource(element), keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setId(id);
        return element;
    }

    @Override
    public long updateElement(Role element) {
        return namedParameterJdbcTemplate.update(UPDATE_ROLE, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public long deleteElement(Role element) {
        return namedParameterJdbcTemplate.update(DELETE_ROLE, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public List<Role> getElements(Role element) {
        List<Role> roles = namedParameterJdbcTemplate.query(SEARCH_ROLE, new MapSqlParameterSource("name", element.getName()), roleRowMapper);
        if (roles.isEmpty()) {
            throw new SSOException("не найдено ролей");
        }
        return roles;
    }

    @Override
    public boolean giveUserRole(Role role, User user) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id_role", role.getId());
        parameters.addValue("id_user", user.getId());

        namedParameterJdbcTemplate.update(GIVE_USER_ROLE, parameters);
        return true;
    }

    private static class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InfoRole(
                    rs.getLong("ROLE_ID"),
                    rs.getString("ROLE_NAME")
            );
        }
    }
}
