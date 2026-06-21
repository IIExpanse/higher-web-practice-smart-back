package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ChatResponse {
    private final UUID chatId;
    private final String message;
    private final String extractedResult;
}
