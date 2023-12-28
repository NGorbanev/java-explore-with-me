package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.AdminActions;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortTypes;
import ru.practicum.ewm.event.enums.UserActions;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventMapper;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.UpdateRequestStatusDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusResultDto;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.ewm.Constants.EVENT_URI;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
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
    @Transactional(readOnly = true)
    public FullEventDto findUserEventById(Long userId, Long eventId) {
        log.info("FindUserEventById is servicing.. UserId={}, eventId={}", userId, eventId);
        checkUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EventNotFoundException(String.format("EventId=%s and userId=%s was not found", eventId, userId)));
        Map<Long, Long> views = getEventsViews(List.of(eventId));
        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    public FullEventDto userUpdateEvent(Long userId, Long eventId, EventUpdateDto eventUpdate) {
        log.info("UserUpdateEvent is servicing.. UserId={}, eventId={}, eventUpdate={}", userId, eventId, eventUpdate.toString());
        checkUser(userId);
        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EventNotFoundException(String.format("EventId=%s and userId=%s was not found", eventId, userId)));
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Only events at PENDING state can be updated");
        }
        if (eventUpdate.getEventDate() != null) {
            LocalDateTime updateEventTime = LocalDateTime.parse(eventUpdate.getEventDate(), DATE_TIME_FORMATTER);
            validateEventTimeByUser(updateEventTime);
        }
        if (eventUpdate.getState() != null) {
            UserActions stateAction;
            try {
                stateAction = UserActions.valueOf(eventUpdate.getState());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException(String.format("Unknown state %s", eventUpdate.getState()));
            }
            switch (stateAction) {
                case TO_REVIEW:
                    oldEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setState(EventState.CANCELED);
                    break;
                default:
                    throw new InvalidRequestException(String.format("Unknown event state: %s", eventUpdate.getState()));
            }
        }
        if (eventUpdate.getAnnotation() != null) {
            oldEvent.setAnnotation(eventUpdate.getAnnotation());
        }
        if (eventUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(
                    () -> new CategoryNotFoundException(String.format("Category %s was not found", eventUpdate.getCategory())));
            oldEvent.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) {
            oldEvent.setDescription(eventUpdate.getDescription());
        }
        if (eventUpdate.getLocation() != null) {
            oldEvent.setLat(eventUpdate.getLocation().getLat());
            oldEvent.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) {
            oldEvent.setIsPaid(eventUpdate.getPaid());
        }
        if (eventUpdate.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventUpdate.getParticipantLimit());
        }
        if (eventUpdate.getRequestModeration() != null) {
            oldEvent.setRequestModeration(eventUpdate.getRequestModeration());
        }
        if (eventUpdate.getTitle() != null) {
            oldEvent.setTitle(eventUpdate.getTitle());
        }
        Event updated = eventRepository.save(oldEvent);
        Map<Long, Long> views = getEventsViews(List.of(eventId));
        log.info("EventId={} was updated by userId={}", eventId, userId);
        return EventMapper.toEventFullDto(updated, views);
    }

    @Override
    public FullEventDto adminUpdateEvent(Long eventId, EventUpdateDto eventUpdate) {
        log.info("AdminUpdateEvent is servicing.. EventId={}, eventUpdate={}", eventId, eventUpdate.toString());
        Event oldEvent = checkEvent(eventId);
        if (eventUpdate.getEventDate() != null) {
            LocalDateTime updateTime = LocalDateTime.parse(eventUpdate.getEventDate(), DATE_TIME_FORMATTER);
            if (updateTime.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new InvalidRequestException("Update event start must be more than in an hour");
            }
        }
        if (eventUpdate.getState() != null) {
            if (!oldEvent.getState().equals(EventState.PENDING)) {
                throw new DataConflictException("Only event in PENDING state can be updated");
            }
            AdminActions stateAction;
            try {
                stateAction = AdminActions.valueOf(eventUpdate.getState());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException(String.format("Unknown state: %s", eventUpdate.getState()));
            }
            switch (stateAction) {
                case REJECT:
                    if (oldEvent.getState().equals(EventState.PUBLISHED)) {
                        throw new InvalidRequestException("Rejected events cant be published");
                    }
                    oldEvent.setState(EventState.CANCELED);
                    break;
                case PUBLISH:
                    if (!oldEvent.getState().equals(EventState.PENDING)) {
                        throw new InvalidRequestException("Only event in PENDING state can be updated");
                    }
                    oldEvent.setState(EventState.PUBLISHED);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    throw new InvalidRequestException(String.format("Unknown event state: %s", eventUpdate.getState()));
            }

        }
        if (eventUpdate.getAnnotation() != null) {
            oldEvent.setAnnotation(eventUpdate.getAnnotation());
        }
        if (eventUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(
                    () -> new CategoryNotFoundException(String.format("Category %s was not found", eventUpdate.getCategory())));
            oldEvent.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) {
            oldEvent.setDescription(eventUpdate.getDescription());
        }
        if (eventUpdate.getLocation() != null) {
            oldEvent.setLat(eventUpdate.getLocation().getLat());
            oldEvent.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) {
            oldEvent.setIsPaid(eventUpdate.getPaid());
        }
        if (eventUpdate.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventUpdate.getParticipantLimit());
        }
        if (eventUpdate.getRequestModeration() != null) {
            oldEvent.setRequestModeration(eventUpdate.getRequestModeration());
        }
        if (eventUpdate.getTitle() != null) {
            oldEvent.setTitle(eventUpdate.getTitle());
        }
        Map<Long, Long> views = getEventsViews(List.of(eventId));
        log.info("Событие с id {} обновлено администратором", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(oldEvent), views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FullEventDto> findEventsByAdmin(AdminEventParamsDto params) {
        log.info("FindEventByAdmin request is servicing.. Params={}", params.toString());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (params.getUsers() != null) {
                CriteriaBuilder.In<Long> usersClause = criteriaBuilder.in(root.get("initiator"));
                for (Long user : params.getUsers()) {
                    usersClause.value(user);
                }
                predicates.add(usersClause);
            }
            if (params.getStates() != null) {
                List<EventState> states = new ArrayList<>();
                StringBuilder caseErrorStateName = new StringBuilder();
                try {
                    for (String state : params.getStates()) {
                        caseErrorStateName.append(state);
                        states.add(EventState.valueOf(state));
                        caseErrorStateName.delete(0, caseErrorStateName.length());
                    }
                } catch (IllegalArgumentException e) {
                    throw new InvalidRequestException(String.format("Unknown event state: %s", caseErrorStateName));
                }
                CriteriaBuilder.In<EventState> statesClause = criteriaBuilder.in(root.get("state"));
                for (EventState state : states) {
                    statesClause.value(state);
                }
                predicates.add(statesClause);
            }
            if (params.getCategories() != null) {
                CriteriaBuilder.In<Long> categoriesClause = criteriaBuilder.in(root.get("category"));
                for (Long category : params.getCategories()) {
                    categoriesClause.value(category);
                }
                predicates.add(categoriesClause);
            }
            if (params.getRangeStart() != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), params.getRangeStart()));
            }
            if (params.getRangeEnd() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), params.getRangeEnd()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
        );
        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        Map<Long, Long> views = getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        return EventMapper.toFullDtos(events, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortEventDto> findEventsByPublic(UserEventParamsDto params, HttpServletRequest request) {
        log.info("FindEventsByPublicApi is servicing.. Params={}, HttpRequest={}", params.toString(), request.toString());
        Sort sort = getSortType(params.getSort());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(),
                params.getSize(), sort);
        LocalDateTime checkedRangeStart;
        if (params.getRangeStart() != null
                && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new DataValidationException("Start date should be before end date");
        } else {
            checkedRangeStart = Objects.requireNonNullElseGet(params.getRangeStart(), LocalDateTime::now);
        }
        Specification<Event> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));
            if (params.getText() != null) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                "%" + params.getText().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                "%" + params.getText().toLowerCase() + "%")));
            }
            if (params.getCategories() != null) {
                CriteriaBuilder.In<Long> categoriesClause = criteriaBuilder.in(root.get("category"));
                for (Long category : params.getCategories()) {
                    categoriesClause.value(category);
                }
                predicates.add(categoriesClause);
            }
            if (params.getPaid() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPaid"), params.getPaid()));
            }
            predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), checkedRangeStart));
            if (params.getRangeEnd() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), params.getRangeEnd()));
            }
            if (params.getOnlyAvailable() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
        );
        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        Map<Long, Long> views = getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        return EventMapper.toShortDtos(events, views);
    }

    @Override
    @Transactional(readOnly = true)
    public FullEventDto findPublishedEventById(Long eventId, HttpServletRequest request) {
        log.info("FindPublishedEventById is servicing.. EventId={}, httpRequest={}", eventId, request.toString());
        Map<Long, Long> views = getEventsViews(List.of(eventId));
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException(String.format("No published event with id=%s found", eventId)));
        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    public List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId) {
        log.info("FindUserEventRequests is servicing.. UserId={}, eventId={}", userId, eventId);
        checkUser(userId);
        checkEvent(eventId);
        List<Request> eventRequests = requestRepository.findAllByEventId(eventId);
        return RequestMapper.toDtos(eventRequests);
    }

    @Override
    public UpdateRequestStatusResultDto changeEventRequestsStatus(Long userId, Long eventId,
                                                                  UpdateRequestStatusDto statusUpdate) {
        log.info("ChangeEventRequestStatus is servicing.. UserId={}, EventId={}, params={}",
                userId,
                eventId,
                statusUpdate.toString());
        int requestsCount = statusUpdate.getRequestIds().size();
        checkUser(userId);
        Event event = checkEvent(eventId);
        RequestStatus status = RequestStatus.valueOf(statusUpdate.getStatus());
        List<Request> requests = requestRepository.findByIdIn(statusUpdate.getRequestIds());

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new InvalidRequestException(String.format("UserId={} hasn't eventId={}", userId, event));
        }
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataConflictException("Status can be changed only for PENDING request");
            }
        }
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == 0 || !event.getRequestModeration()
                        || event.getParticipantLimit() > event.getConfirmedRequests() + requestsCount) {
                    requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                    event.setConfirmedRequests(event.getConfirmedRequests() + requestsCount);
                    confirmed.addAll(requests);
                } else if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                    throw new DataConflictException("Event participation limit is reached");
                } else {
                    for (Request request : requests) {
                        if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                            request.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            confirmed.add(request);
                        } else {
                            request.setStatus(RequestStatus.REJECTED);
                            rejected.add(request);
                        }
                    }
                }
                break;
            case REJECTED:
                requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                rejected.addAll(requests);
        }
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        UpdateRequestStatusResultDto result = new UpdateRequestStatusResultDto(RequestMapper.toDtos(confirmed),
                RequestMapper.toDtos(rejected));
        return result;
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

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(String.format("EventId=%s was not found", eventId)));
    }

    private void validateEventTimeByUser(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Date and time of the event is less than 2 hours before event starts");
        }
    }

    private Sort getSortType(String eventSort) {
        SortTypes sort;
        if (eventSort == null) {
            return Sort.by("id");
        }
        try {
            sort = SortTypes.valueOf(eventSort);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Некорректный тип сортировки событий");
        }
        switch (sort) {
            case DATE:
                return Sort.by("eventDate");
            case VIEWS:
                return Sort.by("views");
            default:
                throw new InvalidRequestException("Некорректный тип сортировки событий");
        }
    }
}
