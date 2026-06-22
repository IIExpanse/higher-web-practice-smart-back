package ru.yandex.practicum.smart.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

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

    /**
     * Выполняет динамический выборку из БД.
     *
     * @param outputFields Список полей, которые нужно вернуть
     * @param inputFilters Карта фильтров (Имя_колонки -> Значение)
     * @return Список строк, где каждая строка — это Map<String, String>
     */
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
        log.debug("Executing modifying query");
        jdbcTemplate.execute(sql);
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

