package ru.yandex.practicum.smart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Метод для инициализации динамических апи после перезапуска приложения.
     */
    @PostConstruct
    public void init() {
        apiRepository.findAll().forEach(api -> {
            try {
                this.registerUrl(api.getPath(), RequestMethod.valueOf(api.getMethod()));

            } catch (Exception e) {
                log.warn("Failed to register api with path {} and method {}", api.getPath(), api.getMethod());
            }
        });
        log.info("Finished startup dynamic route initialization");
    }

    public void registerUrl(String path, RequestMethod httpMethod) throws NoSuchMethodException {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(path)
                .methods(httpMethod)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();

        Method targetMethod = DynamicHandlerController.class
                .getDeclaredMethod("handleDynamicRequest", HttpServletRequest.class);

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

