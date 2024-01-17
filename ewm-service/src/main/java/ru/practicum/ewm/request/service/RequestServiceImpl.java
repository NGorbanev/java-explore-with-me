package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.DataConflictException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.RequestNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findUserRequests(Long userId) {
        log.info("Find user id={} requests is servicing", userId);
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return RequestMapper.toDtos(requests);
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("AddRequest by userId={} for eventId={} is servicing..", userId, eventId);
        Request request;
        User requester = checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(String.format("Event id=%s was not found", eventId)));
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        if (!requests.isEmpty()) {
            throw new DataConflictException(
                    String.format("Request for user id=%s for participating at event id=%s is already exists",
                            userId,
                            event));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataConflictException("Event owner can't be event participant at the same time");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Request can be made only for published event");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= getConfirmedRequests(event)) {
            throw new DataConflictException("Unable to create a request. Event's request limit is reached");
        }
        request = RequestMapper.toRequest(requester, event);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            eventRepository.save(event);
        }
        request = requestRepository.save(request);
        log.info("Participation request for userId={} at eventId={} is added", userId, event.getId());
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request id=%s was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("Запрос на участие с id {} был отменён", requestId);
        return RequestMapper.toRequestDto(request);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s was not found", userId)));
    }

    private Integer getConfirmedRequests(Event event) {
        return requestRepository.findAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size();
    }

}
