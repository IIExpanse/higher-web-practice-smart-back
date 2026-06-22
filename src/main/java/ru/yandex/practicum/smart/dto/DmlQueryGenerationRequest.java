package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DmlQueryGenerationRequest {
    @NotNull
    private final UUID chatId;
    @NotNull
    private final UUID apiId;
}
