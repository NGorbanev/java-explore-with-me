package ru.practicum.ewm.service;

import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique, Integer from, Integer size);

    HitDto save(HitDto dto);
}