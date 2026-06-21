package ru.yandex.practicum.smart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DynamicHandlerController {

    // This method handles the logic when a dynamic API endpoint is hit
    public ResponseEntity<Map<String, String>> handleDynamicRequest(HttpServletRequest request) {
        String URI = request.getRequestURI();
        String method = request.getMethod();
        Map<String, String> parameterMap = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().length > 0 ? entry.getValue()[0] : ""
                ));

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Dynamically handled route!",
                "path", URI,
                "method", method
        ));
    }
}
