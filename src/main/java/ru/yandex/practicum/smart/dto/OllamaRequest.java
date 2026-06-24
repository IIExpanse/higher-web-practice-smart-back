package ru.yandex.practicum.smart.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class OllamaRequest {
    private final String model;
    private final List<OllamaMessage> messages;
    private final boolean stream;
    private final boolean json;

    public OllamaRequest(String model, List<OllamaMessage> messages, boolean stream, boolean json) {
        this.model = model;
        this.messages = Collections.unmodifiableList(messages);
        this.stream = stream;
        this.json = json;
    }
}
