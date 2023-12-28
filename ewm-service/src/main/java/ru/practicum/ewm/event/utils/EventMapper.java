package ru.practicum.ewm.event.utils;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.utils.CategoryMapper;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.ShortEventDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.utils.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.DATE_TIME_FORMATTER;

public class EventMapper {
    public static Event toNewEvent(NewEventDto newEventDto, User user, Category category) {
        Event event = Event.builder()
                .initiator(user)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER))
                .title(newEventDto.getTitle())
                .lon(newEventDto.getLocation().getLon())
                .lat(newEventDto.getLocation().getLat())
                .state(EventState.PENDING)
                .createdOn(LocalDateTime.now())
                .confirmedRequests(0)
                .build();

        if (newEventDto.getPaid() != null) {
            event.setIsPaid(newEventDto.getPaid());
        } else {
            event.setIsPaid(false);
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }

        return event;
    }

    public static FullEventDto toEventFullDto(Event event) {
        FullEventDto fullDto = FullEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .build();
        if (event.getPublishedOn() != null) {
            fullDto.setPublishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER));
        }
        return fullDto;
    }

    public static FullEventDto toEventFullDto(Event event, Map<Long, Long> eventViews) {
        FullEventDto fullDto = FullEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(eventViews.get(event.getId()))
                .build();
        if (event.getPublishedOn() != null) {
            fullDto.setPublishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER));
        }
        return fullDto;
    }

    public static List<FullEventDto> toFullDtos(Collection<Event> events, Map<Long, Long> eventViews) {
        return events.stream()
                .map(event -> toEventFullDto(event, eventViews))
                .collect(Collectors.toList());
    }

    public static ShortEventDto toEventShortDto(Event event, Map<Long, Long> eventViews) {
        return ShortEventDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getIsPaid())
                .title(event.getTitle())
                .views(eventViews.get(event.getId()))
                .build();
    }

    public static List<ShortEventDto> toShortDtos(List<Event> events, Map<Long, Long> eventViews) {
        return events.stream()
                .map(event -> toEventShortDto(event, eventViews))
                .collect(Collectors.toList());
    }
}
