package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventMapper;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.InvalidRequestException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusResultDto;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserService;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.practicum.StatsClient;

import static ru.practicum.ewm.Constants.EVENT_URI;
import static ru.practicum.ewm.Constants.DATE_TIME_FORMATTER;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    private final ObjectMapper objectMapper;
    private final Gson gson;

    @Override
    @Transactional(readOnly = true)
    public List<ShortEventDto> findEventsOfUser(Long userId, Integer from, Integer size) {
        log.info("FindEventsOfUser request is servicing.. UserId={}, pagination from={}, size={}", userId, from, size);
        Map<Long, Long> views;
        List<ShortEventDto> userEvents;
        checkUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsOfUser(userId, pageable).getContent();
        views = getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        userEvents = EventMapper.toShortDtos(events, views);
        return userEvents;
    }

    @Override
    public FullEventDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("AddEvent request is servicing.. UserId={}, event={}", userId, newEventDto.toString());
        User user = checkUser(userId);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new CategoryNotFoundException(String.format("Category %s was not found", newEventDto.getCategory())));
        Event event = EventMapper.toNewEvent(newEventDto, user, category);
        validateEventTimeByUser(event.getEventDate());
        event = eventRepository.save(event);
        FullEventDto fullEventDto = EventMapper.toEventFullDto(event);
        fullEventDto.setViews(0L);
        log.info("Event id {} was added successfully", event.getId());
        return fullEventDto;
    }

    @Override
    public FullEventDto findUserEventById(Long userId, Long eventId) {
        return null;
    }

    @Override
    public FullEventDto userUpdateEvent(Long userId, Long eventId, EventUpdateDto eventUpdate) {
        return null;
    }

    @Override
    public FullEventDto adminUpdateEvent(Long eventId, EventUpdateDto eventUpdate) {
        return null;
    }

    @Override
    public List<FullEventDto> findEventsByAdmin(AdminEventParamsDto eventAdminParam) {
        return null;
    }

    @Override
    public List<ShortEventDto> findEventsByPublic(UserEventParamsDto eventUserParam, HttpServletRequest request) {
        return null;
    }

    @Override
    public FullEventDto findPublishedEventById(Long eventId, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId) {
        return null;
    }

    @Override
    public UpdateRequestStatusResultDto changeEventRequestsStatus(Long userId, Long eventId, UpdateRequestStatusDto request) {
        return null;
    }

    @Override
    public List<Event> findAllByIds(List<Long> ids) {
        return null;
    }

    @Override
    public Map<Long, Long> getEventsViews(List<Long> events) {
        List<StatsDto> stats;
        Map<Long, Long> eventsViews = new HashMap<>();
        List<String> uris = new ArrayList<>();

        if (events == null || events.isEmpty()) {
            return eventsViews;
        }
        for (Long id : events) {
            uris.add(EVENT_URI + id);
        }
        ResponseEntity<Object> response = statsClient.getStats(LocalDateTime.now().minusDays(100).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().format(DATE_TIME_FORMATTER), uris, true);
        Object body = response.getBody();
        if (body != null) {
            String json = gson.toJson(body);
            TypeReference<List<StatsDto>> typeRef = new TypeReference<>() {
            };
            try {
                stats = objectMapper.readValue(json, typeRef);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Getting data from Statistic service failed");
            }
            for (Long event : events) {
                eventsViews.put(event, 0L);
            }
            if (!stats.isEmpty()) {
                for (StatsDto stat : stats) {
                    eventsViews.put(Long.parseLong(stat.getUri().split("/", 0)[2]),
                            stat.getHits());
                }
            }
        }
        return eventsViews;
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User id=%s was not found", userId)));
    }

    private void validateEventTimeByUser(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Date and time of the event is less than 2 hours before event starts");
        }
    }
}
