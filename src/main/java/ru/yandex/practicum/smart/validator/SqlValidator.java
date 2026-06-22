package ru.yandex.practicum.smart.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.dao.DataAccessException;

@Component
@RequiredArgsConstructor
public class SqlValidator {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public boolean isValidSql(String sql) {
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            status.setRollbackOnly();
            try {
                jdbcTemplate.execute("EXPLAIN " + sql);
                return true;
            } catch (DataAccessException e) {
                return false;
            }
        }));
    }
}

