package ru.yandex.practicum.smart.exception;

import org.springframework.http.HttpStatus;

public class HttpClientException extends HttpRequestException {
    public HttpClientException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
