package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.DdlQueryGenerationRequest;
import ru.yandex.practicum.smart.dto.DmlQueryGenerationRequest;

/**
 * Сервис для генерации SQL-запросов (DDL и DML).
 * Извлекает SQL из сообщений чата и сохраняет их в базе данных.
 */
public interface DbQueryGenerationService {
    /**
     * Генерирует и сохраняет DML-запрос.
     *
     * @param request запрос на генерацию DML-запроса
     */
    void generateDmlQuery(DmlQueryGenerationRequest request);

    /**
     * Генерирует и сохраняет DDL-запрос, а также выполняет его.
     *
     * @param request запрос на генерацию DDL-запроса
     */
    void generateDdlQuery(DdlQueryGenerationRequest request);
}
