package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.info("400 {}", e.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                "Invalid request parameter",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final Throwable e) {
        log.warn("500 {}", e.getMessage(), e);
        return new ErrorResponse(
                "RuntimeException",
                "INTERNAL_SERVER_ERROR",
                e.getMessage()
        );
    }
}
