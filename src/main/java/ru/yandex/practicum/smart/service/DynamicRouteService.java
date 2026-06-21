package ru.yandex.practicum.smart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import ru.yandex.practicum.smart.controller.DynamicHandlerController;
import ru.yandex.practicum.smart.repository.ApiRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRouteService {
    private final RequestMappingHandlerMapping handlerMapping;
    private final DynamicHandlerController dynamicHandlerController;
    private final ApiRepository apiRepository;

    @PostConstruct
    public void init() {
        apiRepository.findAll().forEach(api -> {
            try {
                this.registerUrl(api.getPath(), RequestMethod.valueOf(api.getMethod()));

            } catch (Exception e) {
                log.warn("Failed to register api with path {} and method {}", api.getPath(), api.getMethod());
            }
        });
    }

    // Register a new path at runtime
    public void registerUrl(String path, RequestMethod httpMethod) throws NoSuchMethodException {
        // 1. Define the routing conditions
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(path)
                .methods(httpMethod)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();

        // 2. Fetch the handling method from our target controller via reflection
        Method targetMethod = DynamicHandlerController.class
                .getDeclaredMethod("handleDynamicRequest", HttpServletRequest.class);

        // 3. Register the mapping into Spring's active registry
        handlerMapping.registerMapping(requestMappingInfo, dynamicHandlerController, targetMethod);
    }

    public void unregisterUrl(String path, RequestMethod httpMethod) {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(path)
                .methods(httpMethod)
                .build();

        handlerMapping.unregisterMapping(requestMappingInfo);
    }
}

