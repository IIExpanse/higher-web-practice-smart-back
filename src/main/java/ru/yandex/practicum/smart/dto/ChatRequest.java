package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Запрос для отправки сообщения в LLM.
 */
@Getter
@Setter
public class ChatRequest {
    /**
     * Текст сообщения от пользователя (обязательное поле).
     */
    @NotBlank
    private String message;
    
    /**
     * ID чата для продолжения диалога.
     * Если не указан, создаётся новый чат.
     */
    private UUID chatId;
}
