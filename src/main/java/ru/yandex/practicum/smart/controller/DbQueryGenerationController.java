package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.dto.DdlQueryGenerationRequest;
import ru.yandex.practicum.smart.dto.DmlQueryGenerationRequest;
import ru.yandex.practicum.smart.service.DbQueryGenerationService;

import javax.validation.Valid;

/**
 * Контроллер для генерации SQL-запросов (DDL и DML).
 * Обрабатывает описания запросов от LLM и сохраняет их в базу данных.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DbQueryGenerationController {

    private final DbQueryGenerationService dbQueryGenerationService;

    /**
     * Генерирует и сохраняет DML-запрос.
     *
     * @param request валидированный запрос на генерацию DML-запроса
     */
    @PostMapping("/generate/dml")
    public void generateDmlQuery(@RequestBody @Valid DmlQueryGenerationRequest request) {
        dbQueryGenerationService.generateDmlQuery(request);
    }

    /**
     * Генерирует и сохраняет DDL-запрос, а также выполняет его.
     *
     * @param request валидированный запрос на генерацию DDL-запроса
     */
    @PostMapping("/generate/ddl")
    public void generateDdlQuery(@RequestBody @Valid DdlQueryGenerationRequest request) {
        dbQueryGenerationService.generateDdlQuery(request);
    }
}
