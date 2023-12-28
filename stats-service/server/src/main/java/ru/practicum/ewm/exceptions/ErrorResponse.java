package ru.practicum.ewm.exceptions;

import ru.practicum.ewm.mappers.DateTimeMapper;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = DateTimeMapper.toStringDate(LocalDateTime.now());
    }
}
