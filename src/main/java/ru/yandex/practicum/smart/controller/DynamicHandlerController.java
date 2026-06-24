package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.service.DynamicRequestService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки динамических HTTP-запросов.
 * Обрабатывает запросы к API, которые были сгенерированы на основе описаний от LLM.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DynamicHandlerController {
    private final DynamicRequestService dynamicRequestService;

    /**
     * Обрабатывает динамический HTTP-запрос.
     * Извлекает параметры из запроса и передаёт их в сервис для выполнения DML-запроса.
     *
     * @param request HTTP-запрос
     * @return список результатов выполнения запроса
     */
    public List<Map<String, String>> handleDynamicRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        Map<String, String> parameterMap = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().length > 0 ? entry.getValue()[0] : ""
                ));

        return dynamicRequestService.handleDynamicRequest(path, method, parameterMap);
    }
}
