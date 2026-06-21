package ru.yandex.practicum.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.service.ApiGenerationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiGenerationController {
    private final ApiGenerationService apiGenerationService;

    @PostMapping("/generate")
    public void sendMessage(@RequestBody @Valid ApiGenerationRequest request) {
        return chatService.sendMessage(request);
    }
}
