package repository.spring;


import core.domain.authorization.Operation;
import core.domain.authorization.Role;
import exceptions.SSOException;
import interfaces.repository.OperationRepository;
import models.OperationInfo;
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
public class OperationRepositorySpring implements OperationRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OperationRowMapper operationRowMapper;

    @Autowired
    public OperationRepositorySpring(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.operationRowMapper = new OperationRowMapper();
    }

    @Override
    public Operation addElement(Operation element) throws SSOException {
        String query = "INSERT INTO OPERATIONS(OPER_ID , OPER_NAME) VALUES(OPER_SEQ.NEXTVAL, :OPER_NAME )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);

        namedParameterJdbcTemplate.update(query, sqlParameterSource, keyHolder);
        Long id = (Long) keyHolder.getKey();
        element.setOPER_ID(id);
        return element;
    }

    @Override
    public long updateElement(Operation element) throws SSOException {
        final String query = "UPDATE OPERATIONS SET OPER_NAME= :OPER_NAME where OPER_ID = :OPER_ID";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);
        return namedParameterJdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public long deleteElement(Operation element) throws SSOException {
        final String query = "DELETE OPERATIONS WHERE OPER_ID = :OPER_ID";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(element);
        return namedParameterJdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public List<Operation> getElements(Operation element) throws SSOException {
        final String query = "SELECT OPER_ID , OPER_NAME FROM OPERATIONS WHERE OPER_NAME = :OPER_NAME;";
        List<Operation> operation = namedParameterJdbcTemplate.query(query, new MapSqlParameterSource("OPER_NAME", element.getOPER_NAME()), operationRowMapper);
        if (operation.isEmpty()) {
            throw new SSOException("не найдено операции");
        }
        return operation;
    }

    @Override
    public boolean giveRoleOperation(Operation operation, Role role) throws SSOException {
        String query = "INSERT INTO ROLE_OPERATION( RLOP_ID , ROLE_ROLE_ID , OPER_OPER_ID ) VALUES(RLOP_SEQ.NEXTVAL, :ROLE_ID , :OPER_ID )";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ROLE_ID", role.getROLE_ID());
        parameters.addValue("OPER_ID", operation.getOPER_ID());

        namedParameterJdbcTemplate.update(query, parameters);
        return true;
    }

    private static class OperationRowMapper implements RowMapper<Operation> {
        @Override
        public Operation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OperationInfo(
                    rs.getLong("OPER_ID"),
                    rs.getString("OPER_NAME")
            );
        }
    }
}
