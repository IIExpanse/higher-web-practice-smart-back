package ru.yandex.practicum.smart.exception;

import org.springframework.http.HttpStatus;

public class HttpServerException extends HttpRequestException {
    public HttpServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpServerException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
