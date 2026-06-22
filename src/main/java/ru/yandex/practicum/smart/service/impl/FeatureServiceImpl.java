package ru.yandex.practicum.smart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smart.dto.FeatureRequest;
import ru.yandex.practicum.smart.dto.FeatureResponse;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.repository.FeatureRepository;
import ru.yandex.practicum.smart.service.FeatureService;

import java.time.Instant;
import java.util.UUID;

/**
 * Реализация сервиса для управления функциями (features) приложения.
 * Позволяет создавать и сохранять новые функции в базе данных.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    /**
     * Сохраняет новую функцию в базе данных.
     *
     * @param request данные функции (имя)
     * @return сохранённая функция с ID и датой создания
     */
    public FeatureResponse saveFeature(FeatureRequest request) {
        Feature feature = new Feature();
        feature.setId(UUID.randomUUID());
        feature.setName(request.getName());
        feature.setCreatedAt(Instant.now());

        Feature savedFeature = featureRepository.save(feature);
        log.info("Feature saved with id {}", savedFeature.getId());

        return new FeatureResponse(savedFeature.getId(), savedFeature.getName(), savedFeature.getCreatedAt());
    }
}
