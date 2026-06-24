package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OllamaMessage {
    private final String role;
    private final String content;
}
