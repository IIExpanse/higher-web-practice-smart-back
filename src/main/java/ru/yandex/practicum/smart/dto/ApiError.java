package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiError {
    private final String message;
    private final List<String> errors;
}
