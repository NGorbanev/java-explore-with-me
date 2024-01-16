package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findByIdIn(List<Long> requestIds);

    List<Request> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}