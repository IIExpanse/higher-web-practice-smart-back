package ru.yandex.practicum.smart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Config {
    @NotNull
    private ApiConfig config;
}
