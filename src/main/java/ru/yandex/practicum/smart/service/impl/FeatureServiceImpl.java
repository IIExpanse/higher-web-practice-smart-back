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

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureResponse saveFeature(FeatureRequest request) {
        Feature feature = new Feature();
        feature.setId(UUID.randomUUID());
        feature.setName(request.getName());
        feature.setCreatedAt(Instant.now());

        Feature savedFeature = featureRepository.save(feature);
        log.info("Feature saved with id {}", savedFeature.getId());

        FeatureResponse response = new FeatureResponse();
        response.setId(savedFeature.getId());
        response.setName(savedFeature.getName());
        response.setCreatedAt(savedFeature.getCreatedAt());

        return response;
    }
}
