package domain.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import java.sql.SQLException;

/**
 * Транслятор ошибок БД
 */
public class SqlErrorCodeTraslator extends SQLErrorCodeSQLExceptionTranslator {
    @Override
    protected DataAccessException customTranslate(String task, String sql, SQLException sqlEx) {
        if(sqlEx.getErrorCode() == 23505){
            throw new SSOException("запись уже существует", sqlEx);
        }
        return super.customTranslate(task, sql, sqlEx);
    }
}
