package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDto>> getStats(@RequestParam
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                   @RequestParam
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                   @RequestParam(required = false) List<String> uris,
                                                   @RequestParam(defaultValue = "false") Boolean unique) {

        if (start.isAfter(end)) {
            throw new BadRequestException("start date is after end date");
        }
        log.info("Request received: GET /stats");
        return new ResponseEntity<>(service.get(start, end, uris, unique), HttpStatus.OK);
    }

    @PostMapping("/hit")
    public ResponseEntity<HitDto> postHit(@RequestBody HitDto dto) {
        log.info("Request received: POST /hit");
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }
}
