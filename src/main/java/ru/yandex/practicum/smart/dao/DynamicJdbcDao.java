package ru.yandex.practicum.smart.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DynamicJdbcDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

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

        // 1. Формируем список выбираемых полей (Защита от SQL-инъекций: поля должны быть проверенными строками)

        // 2. Динамически строим блок WHERE на основе переданных ключей inputFilters

        // 3. Преобразуем inputFilters в Map<String, Object> для NamedParameterJdbcTemplate
        Map<String, Object> paramSource = new HashMap<>(inputFilters);

        // 4. Выполняем запрос. Spring автоматически мапит строку в Map<String, Object>
        List<Map<String, Object>> rawResultList = jdbcTemplate.queryForList(query, paramSource);

        // 5. Конвертируем выходные значения из Object в String, чтобы получить строго Map<String, String>
        return rawResultList.stream()
                .map(row -> convertRowValuesToString(row, Set.copyOf(outputFields)))
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для приведения всех типов данных БД к String
    private Map<String, String> convertRowValuesToString(Map<String, Object> row, Set<String> outputFields) {
        return row.entrySet().stream()
                .filter(entry -> outputFields.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                ));
    }
}

