package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.comments.utils.CommentMapper;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.DataValidationException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentsServiceImpl implements CommentsService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public CommentShortDto saveComment(Long userId, Long eventId, CommentDto commentDto) {
        log.info("saveComment is servicing.. UserId={}, eventId={}, commentDto={}", userId, eventId, commentDto);
        User user = checkUser(userId);
        Event event = getEvent(eventId);
        if (event.getState() == null) {
            throw new DataValidationException("Event state cant be null");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataValidationException("Comments are available only for PUBLISHED events");
        }
        if (commentDto.getCreated().isAfter(event.getEventDate()) && (event.getRequestModeration() ||
                event.getParticipantLimit() != 0)) {
            boolean rq = requestRepository.existsByRequesterIdAndEventIdAndStatus(userId, eventId,
                    RequestStatus.CONFIRMED);
            if (!rq || !userId.equals(event.getInitiator().getId())) {
                throw new DataValidationException("Only event initiator or visitors can leave a comment");
            }
        }
        Comment after = commentRepository.save(CommentMapper.toComment(user, event, commentDto));
        log.info("Comment was successfully added");
        return CommentMapper.toCommentShortDto(after);
    }

    @Override
    public CommentShortDto updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto) {
        log.info("updateComment is servicing.. UserId={}, eventId={}, commentId={}, commentDto={}",
                userId, eventId, commentId, commentDto);
        checkUser(userId);
        checkEvent(eventId);

        Comment comment = getComment(commentId);

        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ValidationException("Only comment author can edit comments");
        }

        if (comment.getText().equals(commentDto.getText())) {
            throw new ValidationException("Comment was not changed");
        }

        comment.setText(commentDto.getText());
        Comment after = commentRepository.save(comment);
        log.info("Comment update success");
        return CommentMapper.toCommentShortDto(after);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long eventId, Long commentId) {
        log.info("deleteCommentByUser is servicing.. UserId={}, eventId={}, commentId={}", userId, eventId, commentId);
        checkUser(userId);
        Event event = getEvent(eventId);
        Comment comment = getComment(commentId);

        if (userId.equals(event.getInitiator().getId()) || userId.equals(comment.getAuthor().getId())) {
            commentRepository.deleteById(commentId);
            log.info("Comment {} was deleted successfully", commentId);
        } else {
            throw new ValidationException("Only comment author or event initiator can delete comments");
        }
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Comment id={} was deleted by administrator", commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentShortDto getCommentByIdForEvent(Long userId, Long eventId, Long commentId) {
        log.info("getCommentByIdForEvent is servicing.. UserId={}, eventId={}, commentId={}", userId, eventId,commentId);
        checkExistUserById(userId);
        checkEvent(eventId);

        Comment commentById = commentRepository.findByIdForEvent(EventState.PUBLISHED.toString(), eventId, commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment id=%s was not found", commentId)));
        return CommentMapper.toCommentShortDto(commentById);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getCommentsForEvent(Long eventId, int size, int from) {
        log.info("getCommentsForEvent is servicing.. EventId={}, pagination size={}, from={}", eventId, size, from);
        checkEvent(eventId);

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Comment> pageAllComments = commentRepository.findAllByStateAndEventId(EventState.PUBLISHED.toString(),
                eventId, pageRequest);
        return CommentMapper.listToCommentShortDto(pageAllComments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getCommentsForEvent(Long userId, Long eventId, int size, int from) {
        log.info("getCommentsForEvent is servicing.. UserId={}, eventId={}, pagination size={}, from={}",
                userId, eventId, size, from);
        checkUser(userId);
        checkEvent(eventId);

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Comment> pageAllCommentsForEvent = commentRepository.findAllByStateAndEventId(
                EventState.PUBLISHED.toString(), eventId, pageRequest);
        log.info(String.format("Список комментариев к событию (id): %s от пользователя (id) %s: ", eventId, userId));
        return CommentMapper.listToCommentShortDto(pageAllCommentsForEvent);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%s not found", userId)));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment id=%s was not found", commentId)));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event Id=%s was not found", eventId)));
    }

    private void checkEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event id=%s was not found", eventId));
        }
    }

    private void checkExistUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%s was not found", userId));
        }
    }


}
