package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.FeatureRequest;
import ru.yandex.practicum.smart.dto.FeatureResponse;

/**
 * Сервис для управления функциями (features) приложения.
 * Позволяет создавать и сохранять новые функции в базе данных.
 */
public interface FeatureService {
    /**
     * Сохраняет новую функцию в базе данных.
     *
     * @param request данные функции (имя)
     * @return сохранённая функция с ID и датой создания
     */
    FeatureResponse saveFeature(FeatureRequest request);
}
