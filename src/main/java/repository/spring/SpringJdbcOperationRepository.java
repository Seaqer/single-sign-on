package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import interfaces.repository.custom.OperationRepository;
import domain.entity.InfoOperation;
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
 * Интерфейс для работы с операциями в БД через Spring JDBC
 */
@Repository
public class SpringJdbcOperationRepository implements OperationRepository {
    private final String INSERT_OPERATION = "INSERT INTO OPERATIONS(OPER_ID , OPER_NAME) VALUES(OPER_SEQ.NEXTVAL, :name )";
    private final String UPDATE_OPERATION = "UPDATE OPERATIONS SET OPER_NAME= :name where OPER_ID = :id";
    private final String DELETE_OPERATION = "DELETE OPERATIONS WHERE OPER_ID = :id";
    private final String SEARCH_OPERATION = "SELECT OPER_ID , OPER_NAME FROM OPERATIONS WHERE OPER_NAME = :name;";
    private final String GIVE_ROLE_OPERATION = "INSERT INTO ROLE_OPERATION( RLOP_ID , ROLE_ROLE_ID , OPER_OPER_ID ) " +
            "VALUES(RLOP_SEQ.NEXTVAL, :id_role , :id_oper )";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OperationRowMapper operationRowMapper;

    /**
     * Создать репозиторий
     *
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     */
    @Autowired
    public SpringJdbcOperationRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.operationRowMapper = new OperationRowMapper();
    }

    @Override
    public Operation addElement(Operation element) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_OPERATION, new BeanPropertySqlParameterSource(element), keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setId(id);
        return element;
    }

    @Override
    public long updateElement(Operation element) {
        return namedParameterJdbcTemplate.update(UPDATE_OPERATION, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public long deleteElement(Operation element) {
        return namedParameterJdbcTemplate.update(DELETE_OPERATION, new BeanPropertySqlParameterSource(element));
    }

    @Override
    public List<Operation> getElements(Operation element) {
        List<Operation> operation = namedParameterJdbcTemplate.query(SEARCH_OPERATION, new MapSqlParameterSource("name", element.getName()), operationRowMapper);
        if (operation.isEmpty()) {
            throw new SSOException("не найдено операции");
        }
        return operation;
    }

    @Override
    public boolean giveRoleOperation(Operation operation, Role role) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id_role", role.getId());
        parameters.addValue("id_oper", operation.getId());

        namedParameterJdbcTemplate.update(GIVE_ROLE_OPERATION, parameters);
        return true;
    }

    private static class OperationRowMapper implements RowMapper<Operation> {
        @Override
        public Operation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InfoOperation(
                    rs.getLong("OPER_ID"),
                    rs.getString("OPER_NAME")
            );
        }
    }
}
