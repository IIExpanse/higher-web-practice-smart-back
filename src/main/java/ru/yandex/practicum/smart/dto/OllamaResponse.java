package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class OllamaResponse {
    private final String model;
    private final Instant createdAt;
    private final OllamaMessage message;
}
