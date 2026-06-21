package ru.yandex.practicum.smart.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.smart.dto.ApiError;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<ApiError> handleHttpRequestException(HttpRequestException e) {
        return new ResponseEntity<>(new ApiError(e.getMessage(), null), e.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, OllamaResponseParsingException.class})
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ApiError("There are validation errors", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OllamaResponseParsingException.class)
    public ResponseEntity<ApiError> handleOllamaResponseParsingException(OllamaResponseParsingException e) {
        return new ResponseEntity<>(new ApiError(e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
