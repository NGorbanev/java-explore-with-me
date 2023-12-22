package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.Constants.DATE_TIME_FORMATTER;

public class RequestMapper {
    public static Request toRequest(User user, Event event) {
        return Request.builder()
                .requester(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();
    }

    public static ParticipationReqeustDto toRequestDto(Request request) {
        return ParticipationReqeustDto.builder()
                .id(request.getId())
                .created(request.getCreatedAt().format(DATE_TIME_FORMATTER))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<ParticipationReqeustDto> toDtos(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }
}
