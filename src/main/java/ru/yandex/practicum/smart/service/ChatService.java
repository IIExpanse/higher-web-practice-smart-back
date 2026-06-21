package ru.yandex.practicum.smart.service;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smart.dto.ChatRequest;
import ru.yandex.practicum.smart.dto.ChatResponse;

public interface ChatService {
    ChatResponse sendMessage(@RequestBody ChatRequest request);
}
