package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.mappers.HitMapper;
import ru.practicum.ewm.mappers.StatsMapper;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Autowired
    public StatsServiceImpl(StatsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsDto> get(LocalDateTime start,
                              LocalDateTime end,
                              List<String> uris,
                              Boolean unique,
                              Integer from,
                              Integer size) {
        PageRequest pageable = PageRequest.of(from, size);
        if (unique) {
            return StatsMapper.toDtoList(repository.findUniqueStats(start, end, uris, pageable));
        } else {
            return StatsMapper.toDtoList(repository.findStats(start, end, uris, pageable));
        }
    }

    @Override
    public HitDto save(HitDto dto) {
        return HitMapper.toDto(repository.save(HitMapper.toEntity(dto)));
    }
}
