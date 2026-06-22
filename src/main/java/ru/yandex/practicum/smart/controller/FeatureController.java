package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.dto.FeatureRequest;
import ru.yandex.practicum.smart.dto.FeatureResponse;
import ru.yandex.practicum.smart.service.FeatureService;

import javax.validation.Valid;

/**
 * Контроллер для управления функциями (features) приложения.
 * Позволяет создавать и сохранять новые функции в базе данных.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeatureController {
    private final FeatureService featureService;

    /**
     * Сохраняет новую функцию в базе данных.
     *
     * @param request валидированный запрос с именем функции
     * @return сохранённая функция с ID и датой создания
     */
    @PostMapping("/feature")
    public FeatureResponse saveFeature(@RequestBody @Valid FeatureRequest request) {
        return featureService.saveFeature(request);
    }
}
