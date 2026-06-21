package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiConfig {
    @NotBlank
    private final String method;
    @NotBlank
    @URL
    private final String url;
    private final List<String> parameters;
    private final List<String> results;
}
