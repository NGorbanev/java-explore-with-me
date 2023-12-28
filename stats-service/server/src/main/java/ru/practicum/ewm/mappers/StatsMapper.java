package ru.practicum.ewm.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.model.Stats;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StatsMapper {
    public static Stats toEntity(StatsDto dto) {
        return Stats.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .hits(dto.getHits())
                .build();
    }

    public static StatsDto toDto(Stats entity) {
        return StatsDto.builder()
                .app(entity.getApp())
                .uri(entity.getUri())
                .hits(entity.getHits())
                .build();
    }

    public static List<StatsDto> toDtoList(List<Stats> viewStats) {
        return viewStats.stream().map(StatsMapper::toDto).collect(Collectors.toList());
    }
}
