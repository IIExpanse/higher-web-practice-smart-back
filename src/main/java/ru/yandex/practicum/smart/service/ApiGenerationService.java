package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.dto.ApiGenerationResponse;

/**
 * Сервис для генерации API-конфигураций на основе запросов пользователя.
 * Создаёт конфигурации REST-эндпоинтов на основе описаний от LLM.
 */
public interface ApiGenerationService {
    /**
     * Генерирует API-конфигурацию на основе запроса.
     *
     * @param request запрос на генерацию API с идентификаторами чата и функции
     * @return сгенерированная конфигурация API
     */
    ApiGenerationResponse generate(ApiGenerationRequest request);
}
