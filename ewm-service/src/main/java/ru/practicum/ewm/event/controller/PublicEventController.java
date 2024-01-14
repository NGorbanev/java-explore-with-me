package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.dto.ShortEventDto;
import ru.practicum.ewm.event.dto.UserEventParamsDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusResultDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping("/events")
    public List<ShortEventDto> findEventsByPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        UserEventParamsDto eventUserParam = new UserEventParamsDto(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size);
        log.info("{} GET /events. Parameters: {}", API_LOGSTRING, eventUserParam);
        HitDto hitDto = HitDto.builder()
                .app(SERVICE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.save(hitDto);
        return eventService.findEventsByPublic(eventUserParam, request);
    }

    @GetMapping("/events/{eventId}")
    public FullEventDto findPublishedEventById(@PathVariable Long eventId,
                                               HttpServletRequest request) {
        log.info("{} GET /events/{eventId}. EventId={}, request={}", API_LOGSTRING, eventId, request);
        HitDto hitDto = HitDto.builder()
                .app(SERVICE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.save(hitDto);
        return eventService.findPublishedEventById(eventId, request);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("{} GET /users/{userId}/events/{eventId}/requests. UserId={}, EventId={}", API_LOGSTRING, userId, eventId);
        return eventService.findUserEventRequests(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}/requests")
    public UpdateRequestStatusResultDto changeEventRequestsStatus(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @Valid @RequestBody UpdateRequestStatusDto updateRequest) {
        log.info("{} PATCH /users/{userId}/events/{eventId}/requests UserID={}, EventId={}, UpdateRequest={}",
                API_LOGSTRING,
                userId,
                eventId,
                updateRequest.toString());
        return eventService.changeEventRequestsStatus(userId, eventId, updateRequest);
    }
}
