package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class DmlQueryGenerationRequest {
    @NotNull
    private UUID chatId;
    @NotNull
    private UUID apiId;
}
