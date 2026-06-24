package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.dto.ApiGenerationResponse;
import ru.yandex.practicum.smart.service.ApiGenerationService;

import javax.validation.Valid;

/**
 * Контроллер для генерации API-конфигураций на основе запросов пользователя.
 * Обрабатывает описания API от LLM и сохраняет их в базу данных.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiGenerationController {
    private final ApiGenerationService apiGenerationService;

    /**
     * Генерирует и сохраняет API-конфигурацию.
     *
     * @param request валидированный запрос на генерацию API
     * @return сгенерированная конфигурация API
     */
    @PostMapping("/generate/api")
    public ApiGenerationResponse sendMessage(@RequestBody @Valid ApiGenerationRequest request) {
        return apiGenerationService.generate(request);
    }
}
