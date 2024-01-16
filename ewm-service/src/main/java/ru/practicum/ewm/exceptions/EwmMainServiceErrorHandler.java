package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class EwmMainServiceErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public EwmMainServiceErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found exception");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new EwmMainServiceErrorResponse(HttpStatus.NOT_FOUND, "User search failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

}
