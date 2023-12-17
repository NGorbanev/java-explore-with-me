package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    HitDto save(HitDto dto);
}