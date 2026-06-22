package ru.yandex.practicum.smart.service;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smart.dto.ChatRequest;
import ru.yandex.practicum.smart.dto.ChatResponse;

/**
 * Сервис для взаимодействия с LLM (Large Language Model).
 * Обрабатывает запросы пользователей и возвращает ответы от модели.
 */
public interface ChatService {
    /**
     * Отправляет сообщение в LLM и получает ответ.
     *
     * @param request запрос с сообщением пользователя и ID чата
     * @return ответ от LLM
     */
    ChatResponse sendMessage(@RequestBody ChatRequest request);
}
