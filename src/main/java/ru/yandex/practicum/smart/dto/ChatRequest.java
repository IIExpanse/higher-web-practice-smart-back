package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ChatRequest {
    @NotBlank
    private final String message;
    private final UUID chatId;
}
