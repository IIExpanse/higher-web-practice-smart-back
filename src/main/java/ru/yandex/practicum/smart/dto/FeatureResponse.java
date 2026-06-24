package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class FeatureResponse {
    private final UUID featureId;
    private final String name;
    private final Instant createdAt;
}
