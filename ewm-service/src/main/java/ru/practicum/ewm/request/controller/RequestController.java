package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> findUserRequests(@PathVariable Long userId) {
        log.info("{} GET /users/{userId}/requests. UserId={}", API_LOGSTRING, userId);
        return requestService.findUserRequests(userId);
    }

    @PostMapping(value = "/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @NotNull @RequestParam Long eventId) {
        log.info("{} POST /users/{userId}/requests. UserID={}, eventId={}", API_LOGSTRING, userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("{} PATCH /users/{userId}/requests/{requestId}/cancel UserId={}, requestId={}", API_LOGSTRING, userId, requestId);
        return requestService.rejectRequest(userId, requestId);
    }
}
