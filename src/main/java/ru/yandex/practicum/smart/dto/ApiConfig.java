package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ApiConfig {
    @NotBlank
    private String method;
    @NotBlank
    @URL
    private String url;
    private List<String> parameters;
    private List<String> results;
}
