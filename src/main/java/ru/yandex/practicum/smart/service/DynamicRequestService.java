package ru.yandex.practicum.smart.service;

import java.util.List;
import java.util.Map;

public interface DynamicRequestService {
    List<Map<String, String>> handleDynamicRequest(String path, String method, Map<String, String> parameters);
}
