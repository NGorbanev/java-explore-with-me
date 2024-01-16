package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequest;
import ru.practicum.ewm.compilation.dto.IncomingCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.utils.CompilationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exceptions.CompilationNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("FindCompilation is servicing.. Pinned={}, pagination from={}, size={}", pinned, from, size);
        Set<Event> eventSet = new HashSet<>();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByIsPinned(pinned, pageable);
        for (Compilation compilation : compilations) {
            eventSet.addAll(compilation.getEvents());
        }
        List<Long> eventIds = eventSet.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = eventService.getEventsViews(eventIds);
        return CompilationMapper.toDtos(compilations, views);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findCompilationById(Long compilationId) {
        log.info("FindCompilationById is servicing.. CompilationId={}", compilationId);
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(
                        String.format("CompilationId=%s was not found", compilationId)));
        List<Long> events = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = eventService.getEventsViews(events);
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    public CompilationDto addCompilation(IncomingCompilationDto incomingCompilationDto) {
        log.info("AddCompilation is servicing.. Compilation={}", incomingCompilationDto);
        List<Event> events;
        Map<Long, Long> views = new HashMap<>();
        if (incomingCompilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(incomingCompilationDto.getEvents());
            views = eventService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        } else {
            events = new ArrayList<>();
        }
        Compilation compilation = CompilationMapper.toNewCompilation(incomingCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.info("DeleteCompilation is servicing.. CompilationId={}", compId);
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info("Compilation id={} was not found", compId);
        }
        log.info("DeleteCompilation success. CompilationId={} was deleted", compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, CompilationUpdateRequest update) {
        log.info("UpdateCompilation is servicing.. CompilationId={}, CompilationUpdate={}", compId, update.toString());
        Compilation oldCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(
                        String.format("CompilationId=%s was not found", compId)));
        Map<Long, Long> views = new HashMap<>();
        if (update.getEvents() != null && !update.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllByIdIn(update.getEvents());
            oldCompilation.setEvents(events);
            views = eventService.getEventsViews(update.getEvents());
        }
        if (update.getPinned() != null) {
            oldCompilation.setIsPinned(update.getPinned());
        }
        if (update.getTitle() != null) {
            oldCompilation.setTitle(update.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(oldCompilation), views);
    }
}
