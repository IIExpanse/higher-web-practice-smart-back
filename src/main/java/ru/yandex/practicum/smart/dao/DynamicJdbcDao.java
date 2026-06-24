package ru.yandex.practicum.smart.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.smart.exception.HttpClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicJdbcDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Map<String, String>> executeDynamicQuery(
            String query,
            List<String> outputFields,
            Map<String, String> inputFilters) {

        Map<String, Object> paramSource = new HashMap<>(inputFilters);

        log.debug("Executing dynamic query: {}", query);
        List<Map<String, Object>> rawResultList = parameterJdbcTemplate.queryForList(query, paramSource);

        return rawResultList.stream()
                .map(row -> convertRowValuesToString(row, Set.copyOf(outputFields)))
                .collect(Collectors.toList());
    }

    public void executeModifyingQuery(String sql) {
        try {
            log.debug("Executing modifying query");
            jdbcTemplate.execute(sql);

        } catch (Exception e) {
            log.error("Error executing modifying query", e);
            throw new HttpClientException("There was an error executing query, check if it is valid");
        }

    }

    private Map<String, String> convertRowValuesToString(Map<String, Object> row, Set<String> outputFields) {
        return row.entrySet().stream()
                .filter(entry -> outputFields.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                ));
    }
}

