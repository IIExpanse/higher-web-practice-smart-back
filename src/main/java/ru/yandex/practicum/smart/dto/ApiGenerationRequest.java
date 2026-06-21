package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ApiGenerationRequest {
    @NotNull
    private final ApiConfig config;
    @NotNull
    private final UUID featureId;
}
