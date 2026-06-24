package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ApiGenerationResponse {
    private final UUID chatId;
    private final UUID featureId;
    private final UUID apiId;
    private final String path;
    private final String method;
    private final List<String> parameters;
    private final List<String> results;
}
