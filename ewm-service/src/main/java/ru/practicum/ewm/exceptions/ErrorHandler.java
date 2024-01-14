package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Parameter validation failed");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.BAD_REQUEST, "Parameter validation failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDataValidationException(DataValidationException e) {
        log.error("Incoming data error", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.BAD_REQUEST, "Incoming data error", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Database data error");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.CONFLICT, "Incoming data error",
                e.getMessage(), Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConflictException(DataConflictException e) {
        log.error("Conflict while servicing the request");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.CONFLICT, "Incoming data servicing conflict",
                e.getMessage(), Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(CategoryNotFoundException e) {
        log.error("Category search failed", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "Category search failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(UserNotFoundException e) {
        log.error("User search failed");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "User search failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEventNotFoundException(EventNotFoundException e) {
        log.error("Event search failed");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "Event search failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompilationNotFoundException(CompilationNotFoundException e) {
        log.error("Compilation search failed");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "Compilation search failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRequestNotFoundException(RequestNotFoundException e) {
        log.error("Request searching failed");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "Request searching failed", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("Needed parameter is missed at request");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.BAD_REQUEST, "Needed parameter is missed at request", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(InvalidRequestException e) {
        log.error("Incorrect request");
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrect request", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownException(Throwable e) {
        log.error("Unexpected error", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", e.getMessage(),
                Collections.singletonList(stackTrace), LocalDateTime.now());
    }
}
