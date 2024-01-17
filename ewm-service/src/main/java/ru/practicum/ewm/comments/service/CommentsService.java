package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentShortDto;

import java.util.List;

public interface CommentsService {
    CommentShortDto saveComment(Long userId, Long eventId, CommentDto commentDto);

    CommentShortDto updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto);

    void deleteCommentByUser(Long userId, Long eventId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentShortDto getCommentByIdForEvent(Long userId, Long eventId, Long commentId);

    List<CommentShortDto> getCommentsForEvent(Long eventId, int size, int from);

    List<CommentShortDto> getCommentsForEvent(Long userId, Long eventId, int size, int from);
}
