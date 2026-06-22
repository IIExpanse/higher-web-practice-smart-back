package ru.yandex.practicum.smart.service;

import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.dto.ApiGenerationResponse;

public interface ApiGenerationService {
    ApiGenerationResponse generate(ApiGenerationRequest request);}
