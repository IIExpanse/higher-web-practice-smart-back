package ru.yandex.practicum.smart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.smart.dto.ApiConfig;
import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.dto.ApiGenerationResponse;
import ru.yandex.practicum.smart.dto.Config;
import ru.yandex.practicum.smart.exception.HttpClientException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.ApiParameter;
import ru.yandex.practicum.smart.model.entity.ApiResult;
import ru.yandex.practicum.smart.model.entity.Chat;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.model.entity.Message;
import ru.yandex.practicum.smart.repository.ApiParameterRepository;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.ApiResultRepository;
import ru.yandex.practicum.smart.repository.ChatRepository;
import ru.yandex.practicum.smart.repository.FeatureRepository;
import ru.yandex.practicum.smart.repository.MessageRepository;
import ru.yandex.practicum.smart.service.ApiGenerationService;
import ru.yandex.practicum.smart.service.DynamicRouteService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для генерации API-конфигураций на основе запросов пользователя.
 * Создаёт конфигурации REST-эндпоинтов на основе описаний от LLM и регистрирует динамические маршруты.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiGenerationServiceImpl implements ApiGenerationService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final FeatureRepository featureRepository;
    private final DynamicRouteService dynamicRouteService;
    private final ApiRepository apiRepository;
    private final ApiParameterRepository apiParameterRepository;
    private final ApiResultRepository apiResultRepository;
    private final ObjectMapper objectMapper;

    /**
     * Генерирует API-конфигурацию на основе запроса.
     * Извлекает конфигурацию из последнего сообщения чата, сохраняет API, параметры и результаты,
     * а также регистрирует динамический маршрут.
     *
     * @param request запрос на генерацию API с идентификаторами чата и функции
     * @return сгенерированная конфигурация API
     */
    @Override
    @Transactional
    public ApiGenerationResponse generate(ApiGenerationRequest request) {
        log.debug("Starting api generation for feature={} and chat={}", request.getFeatureId(), request.getChatId());

        Feature feature = featureRepository.findById(request.getFeatureId()).orElse(null);
        if (feature == null) {
            throw new HttpClientException("Feature not found by id");
        }
        ApiConfig config = getConfig(request);

        if (!isValidHttpMethod(config.getMethod())) {
            throw new HttpClientException("Invalid HTTP method in extracted content");
        }
        Api api = new Api();
        api.setId(UUID.randomUUID());
        api.setFeature(feature);
        api.setMethod(config.getMethod().strip().toUpperCase());
        api.setPath(config.getUrl().strip());
        api.setCreatedAt(Instant.now());

        api = apiRepository.save(api);
        saveApiParameters(api, config);
        saveApiResults(api, config);
        log.debug("Finished saving api data for feature={} and chat={}", request.getFeatureId(), request.getChatId());

        try {
            dynamicRouteService.registerUrl(api.getPath(), RequestMethod.valueOf(api.getMethod()));
            log.info("Registered new api with path {} and method {}", api.getPath(), api.getMethod());
        } catch (NoSuchMethodException e) {
            throw new HttpClientException(e.getMessage());
        }
        return new ApiGenerationResponse(
                request.getChatId(),
                request.getFeatureId(),
                api.getId(),
                api.getPath(),
                api.getMethod(),
                config.getParameters(),
                config.getResults()
        );
    }

    /**
     * Извлекает API-конфигурацию из последнего сообщения чата.
     *
     * @param request запрос на генерацию API
     * @return API-конфигурация из сообщения
     */
    private ApiConfig getConfig(ApiGenerationRequest request) {
        Chat chat = chatRepository.findById(request.getChatId()).orElse(null);
        if (chat == null) {
            throw new HttpClientException(String.format("Chat with id %s does not exist", request.getChatId()));
        }
        Message message = messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chat.getId())
                .orElse(null);
        if (message == null) {
            throw new HttpClientException("Failed to find chat message with non-empty extracted content");
        }

        ApiConfig config = parseConfig(message.getExtractedContent()).orElse(null);
        if (config == null) {
            throw new HttpClientException(String.format("Extracted content from last message with id=%s " +
                    "with chatId=%s cannot ba parsed as ApiConfig", message.getId(), message.getChat().getId()));
        }
        return config;
    }

    /**
     * Сохраняет параметры API в базе данных.
     *
     * @param api       API, для которого сохраняются параметры
     * @param config    API-конфигурация с параметрами
     */
    private void saveApiParameters(Api api, ApiConfig config) {
        if (config.getParameters() == null || config.getParameters().isEmpty()) {
            return;
        }
        List<ApiParameter> apiParameters = config.getParameters().stream()
                .map(param -> param.strip().toLowerCase())
                .map(param -> {
                    ApiParameter apiParameter = new ApiParameter();
                    apiParameter.setId(UUID.randomUUID());
                    apiParameter.setApi(api);
                    apiParameter.setName(param);
                    apiParameter.setCreatedAt(Instant.now());

                    return apiParameter;
                })
                .collect(Collectors.toList());

        apiParameterRepository.saveAll(apiParameters);
    }

    /**
     * Сохраняет результаты API в базе данных.
     *
     * @param api       API, для которого сохраняются результаты
     * @param config    API-конфигурация с результатами
     */
    private void saveApiResults(Api api, ApiConfig config) {
        if (config.getResults() == null || config.getResults().isEmpty()) {
            return;
        }
        List<ApiResult> apiResults = config.getResults().stream()
                .map(result -> result.strip().toLowerCase())
                .map(result -> {
                    ApiResult apiResult = new ApiResult();
                    apiResult.setId(UUID.randomUUID());
                    apiResult.setApi(api);
                    apiResult.setName(result);
                    apiResult.setCreatedAt(Instant.now());

                    return apiResult;
                })
                .collect(Collectors.toList());

        apiResultRepository.saveAll(apiResults);
    }

    /**
     * Проверяет, является ли метод HTTP-методом.
     *
     * @param httpMethod строковое представление HTTP-метода
     * @return true, если метод валиден, иначе false
     */
    private boolean isValidHttpMethod(String httpMethod) {
        try {
            RequestMethod.valueOf(httpMethod.strip().toUpperCase());

        } catch (IllegalArgumentException e) {
            log.debug("Provided httpMethod={} is not valid", httpMethod);
            return false;
        }
        return true;
    }

    /**
     * Парсит JSON-строку в API-конфигурацию.
     *
     * @param extractedContent JSON-строка с конфигурацией
     * @return Optional с API-конфигурацией или пустой Optional
     */
    private Optional<ApiConfig> parseConfig(String extractedContent) {
        try {
            ApiConfig config = objectMapper.readValue(extractedContent, Config.class).getConfig();
            return Optional.of(config);

        } catch (JsonProcessingException e) {
            log.debug("Failed to parse ApiConfig from extracted content: {}", extractedContent);
            return Optional.empty();
        }
    }
}
