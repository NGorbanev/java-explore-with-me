package ru.practicum.ewm.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constants.TIME_FORMAT;

@Data
@RequiredArgsConstructor
public class EwmMainServiceErrorResponse {
    private final HttpStatus status;
    private final String reason;
    private final String message;
    private final List<String> errors;
    @JsonFormat(pattern = TIME_FORMAT)
    private final LocalDateTime timestamp;
}
