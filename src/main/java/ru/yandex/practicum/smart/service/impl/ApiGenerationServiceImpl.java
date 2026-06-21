package ru.yandex.practicum.smart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.smart.dto.ApiConfig;
import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.exception.HttpClientException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.FeatureRepository;
import ru.yandex.practicum.smart.service.ApiGenerationService;
import ru.yandex.practicum.smart.service.DynamicRouteService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiGenerationServiceImpl implements ApiGenerationService {
    private final FeatureRepository featureRepository;
    private final DynamicRouteService dynamicRouteService;
    private final ApiRepository apiRepository;

    @Override
    public void generate(ApiGenerationRequest request) {
        ApiConfig config = request.getConfig();

        if (!isValidHttpMethod(config.getMethod())) {
            throw new HttpClientException("Invalid HTTP Method");
        }
        Feature feature = featureRepository.findById(request.getFeatureId()).orElse(null);
        if (feature == null) {
            throw new HttpClientException("Feature not found by id");
        }
        Api api = new Api();
        api.setId(UUID.randomUUID());
        api.setFeature(feature);
        api.setMethod(config.getMethod().strip().toUpperCase());
        api.setPath(config.getUrl().strip());
        api.setCreatedAt(Instant.now());

        try {
            dynamicRouteService.registerUrl(api.getPath(), RequestMethod.valueOf(api.getMethod()));

        } catch (NoSuchMethodException e) {
            throw new HttpClientException(e.getMessage());
        }
        apiRepository.save(api);

    }

    private boolean isValidHttpMethod(String httpMethod) {
        try {
            RequestMethod.valueOf(httpMethod.strip().toUpperCase());

        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
