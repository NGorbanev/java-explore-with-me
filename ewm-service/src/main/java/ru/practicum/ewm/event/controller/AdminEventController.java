package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminEventParamsDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;
import static ru.practicum.ewm.Constants.TIME_FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping("/admin/events")
    public List<FullEventDto> findEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                                LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                                LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        AdminEventParamsDto adminEventParamsDto = new AdminEventParamsDto(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
        log.info("{} GET /admin/events adminEventParams={}", API_LOGSTRING, adminEventParamsDto);
        return eventService.findEventsByAdmin(adminEventParamsDto);
    }

    @PatchMapping(value = "/admin/events/{eventId}")
    public FullEventDto adminUpdateEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody EventUpdateDto updateRequest) {
        log.info("{} PATCH /admin/events/{eventId} EventId={}, updateRequest={}", API_LOGSTRING, eventId, updateRequest.toString());
        return eventService.adminUpdateEvent(eventId, updateRequest);
    }
}
