package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.dto.ChatRequest;
import ru.yandex.practicum.smart.dto.ChatResponse;
import ru.yandex.practicum.smart.service.ChatService;

import javax.validation.Valid;

/**
 * Контроллер для обработки запросов к LLM.
 * Обрабатывает сообщения пользователей и возвращает ответы от модели.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * Отправляет сообщение в LLM и получает ответ.
     *
     * @param request валидированный запрос с сообщением пользователя
     * @return ответ от LLM
     */
    @PostMapping("/chat")
    public ChatResponse sendMessage(@RequestBody @Valid ChatRequest request) {
        return chatService.sendMessage(request);
    }
}
