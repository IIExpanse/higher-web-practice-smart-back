package ru.yandex.practicum.smart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smart.dao.DynamicJdbcDao;
import ru.yandex.practicum.smart.exception.HttpServerException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.ApiParameter;
import ru.yandex.practicum.smart.model.entity.ApiResult;
import ru.yandex.practicum.smart.model.entity.DmlQuery;
import ru.yandex.practicum.smart.repository.ApiParameterRepository;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.ApiResultRepository;
import ru.yandex.practicum.smart.repository.DmlQueryRepository;
import ru.yandex.practicum.smart.service.DynamicRequestService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRequestServiceImpl implements DynamicRequestService {
    private final ApiRepository apiRepository;
    private final DmlQueryRepository dmlQueryRepository;
    private final DynamicJdbcDao dynamicJdbcDao;
    private final ApiParameterRepository apiParameterRepository;
    private final ApiResultRepository apiResultRepository;

    @Override
    public List<Map<String, String>> handleDynamicRequest(String path, String method, Map<String, String> parameters) {
        log.debug("Executing dynamic query for path={} and method={}", path, method);

        Api api = apiRepository.findByMethodAndPath(method, path).orElse(null);
        if (api == null) {
            throw new HttpServerException(
                    String.format("Api for method %s and path %s was not found in database", method, path)
            );
        }
        DmlQuery dmlQuery = dmlQueryRepository.findFirstByApi_Id(api.getId()).orElse(null);
        if (dmlQuery == null) {
            return List.of(Map.of("message", "bound query for invoked api does not exist"));
        }

        Set<String> storedParameters = apiParameterRepository.findAllByApi_Id(api.getId()).stream()
                .map(ApiParameter::getName)
                .collect(Collectors.toSet());

        Map<String, String> filteredMap = parameters.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().toLowerCase(), entry.getValue()))
                .filter(entry -> storedParameters.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        storedParameters.forEach(param -> filteredMap.putIfAbsent(param, null));

        List<String> results = apiResultRepository.findAllByApi_Id(api.getId()).stream()
                .map(ApiResult::getName)
                .collect(Collectors.toList());

        return dynamicJdbcDao.executeDynamicQuery(dmlQuery.getQuery(), results, filteredMap);
    }
}
