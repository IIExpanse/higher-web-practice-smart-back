package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.DdlQueryGenerationRequest;
import ru.yandex.practicum.smart.dto.DmlQueryGenerationRequest;

public interface DbQueryGenerationService {
    void generateDmlQuery(DmlQueryGenerationRequest request);

    void generateDdlQuery(DdlQueryGenerationRequest request);
}
