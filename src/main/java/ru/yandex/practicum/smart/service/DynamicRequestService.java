package ru.yandex.practicum.smart.service;

import java.util.List;
import java.util.Map;

/**
 * Сервис для обработки динамических HTTP-запросов.
 * Выполняет DML-запросы на основе конфигурации API, переданной в параметрах URL.
 */
public interface DynamicRequestService {
    /**
     * Обрабатывает динамический HTTP-запрос.
     *
     * @param path       путь запроса
     * @param method     HTTP-метод запроса
     * @param parameters параметры запроса
     * @return список результатов выполнения запроса
     */
    List<Map<String, String>> handleDynamicRequest(String path, String method, Map<String, String> parameters);
}
