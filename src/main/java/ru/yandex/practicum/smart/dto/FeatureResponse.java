package ru.yandex.practicum.smart.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FeatureResponse {
    private UUID id;
    private String name;
    private Instant createdAt;
}
