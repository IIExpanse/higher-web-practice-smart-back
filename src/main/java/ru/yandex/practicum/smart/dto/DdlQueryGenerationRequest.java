package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class DdlQueryGenerationRequest {
    @NotNull
    private UUID featureId;
    @NotNull
    private UUID chatId;
}
