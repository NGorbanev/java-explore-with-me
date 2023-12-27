package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.ShortEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping(value = "/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("{} POST /users/{userId}/events UserId={}, newEventDto={}", API_LOGSTRING, userId, newEventDto.toString());
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<ShortEventDto> findEventsOfUser(@PathVariable Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("{} GET /users/{userId}/events. UserId={}, from={}, size={}", API_LOGSTRING, userId, from, size);
        return eventService.findEventsOfUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public FullEventDto findUserEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("{} GET /users/{userId}/events/{eventId}. UserId={}, eventId={}", API_LOGSTRING, userId, eventId);
        return eventService.findUserEventById(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public FullEventDto userUpdateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        log.info("{} PATCH /users/{userId}/events/{eventId}. UserId={}, EventId={}, eventUpdateDto={}",
                API_LOGSTRING,
                userId,
                eventId,
                eventUpdateDto.toString());
        return eventService.userUpdateEvent(userId, eventId, eventUpdateDto);
    }
}
