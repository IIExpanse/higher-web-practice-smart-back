package ru.yandex.practicum.smart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpRequestException extends RuntimeException {
    private final HttpStatus httpStatus;

    public HttpRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpRequestException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
