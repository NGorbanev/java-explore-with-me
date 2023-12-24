package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusDto;
import ru.practicum.ewm.request.dto.UpdateRequestStatusResultDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<ShortEventDto> findEventsOfUser(Long userId, Integer from, Integer size);

    FullEventDto addEvent(Long userId, NewEventDto newEventDto);

    FullEventDto findUserEventById(Long userId, Long eventId);

    FullEventDto userUpdateEvent(Long userId, Long eventId, EventUpdateDto eventUpdate);

    FullEventDto adminUpdateEvent(Long eventId, EventUpdateDto eventUpdate);

    List<FullEventDto> findEventsByAdmin(AdminEventParamsDto eventAdminParam);

    List<ShortEventDto> findEventsByPublic(UserEventParamsDto eventUserParam, HttpServletRequest request);

    FullEventDto findPublishedEventById(Long eventId, HttpServletRequest request);

    List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId);

    UpdateRequestStatusResultDto changeEventRequestsStatus(Long userId, Long eventId, UpdateRequestStatusDto request);

    List<Event> findAllByIds(List<Long> ids);

    Map<Long, Long> getEventsViews(List<Long> events);

}
