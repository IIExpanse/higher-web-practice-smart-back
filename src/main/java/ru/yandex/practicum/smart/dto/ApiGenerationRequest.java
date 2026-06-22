package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Запрос на генерацию API-конфигурации.
 * Содержит идентификаторы чата и функции для контекста генерации.
 */
@Getter
@Setter
public class ApiGenerationRequest {
    /**
     * ID чата (обязательное поле).
     */
    @NotNull
    private UUID chatId;
    
    /**
     * ID функции (обязательное поле).
     */
    @NotNull
    private UUID featureId;
}
