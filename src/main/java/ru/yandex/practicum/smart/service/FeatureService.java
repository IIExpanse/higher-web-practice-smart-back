package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.FeatureRequest;
import ru.yandex.practicum.smart.dto.FeatureResponse;

public interface FeatureService {
    FeatureResponse saveFeature(FeatureRequest request);
}
