package ru.yandex.practicum.smart.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.smart.dto.OllamaMessage;
import ru.yandex.practicum.smart.dto.OllamaRequest;
import ru.yandex.practicum.smart.dto.OllamaResponse;

import java.util.List;

/**
 * Клиент для взаимодействия с Ollama API.
 * Отправляет запросы к локальной модели LLM и получает ответы.
 */
@Component
@RequiredArgsConstructor
public class OllamaClient {
    @Value("${smart-back.ollama.api.url}")
    private String ollamaApiUtl;
    @Value("${smart-back.ollama.api.model}")
    private String ollamaLlmModel;

    private final RestTemplate restTemplate;

    /**
     * Отправляет список сообщений в Ollama API и получает ответ.
     *
     * @param messages список сообщений для отправки
     * @return ответ от Ollama API
     */
    public OllamaResponse sendMessage(List<OllamaMessage> messages) {
        OllamaRequest request = new OllamaRequest(
                ollamaLlmModel,
                messages,
                false,
                true
        );

        ResponseEntity<OllamaResponse> response = restTemplate.postForEntity(
                ollamaApiUtl,
                new HttpEntity<>(request, createHeaders()),
                OllamaResponse.class
        );

        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
