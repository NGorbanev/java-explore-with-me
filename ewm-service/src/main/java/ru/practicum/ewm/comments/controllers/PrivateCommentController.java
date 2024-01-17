package ru.practicum.ewm.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.service.CommentsService;

import javax.validation.Valid;

import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
    private final CommentsService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentShortDto saveComment(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @Valid @RequestBody CommentDto commentDto) {
        log.info("{} POST /users/{userId}/events/{eventId}/comments. UserId={}, eventId={}, commentDto={}",
                API_LOGSTRING, userId, eventId, commentDto);
        return commentService.saveComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @PathVariable Long commentId,
                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("{} PATCH /{commentId}. UserId={}, eventId={}, commentId={}, commentDto={}", API_LOGSTRING,
                userId, eventId, commentId, commentDto);
        return commentService.updateComment(userId, eventId, commentId, commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentShortDto getCommentByIdForEvent(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long commentId) {
        log.info("{} /GET /{commentId} UserId={}, eventId={}, commentId={}", API_LOGSTRING, userId, eventId, commentId);
        return commentService.getCommentByIdForEvent(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentShortDto> getCommentsForEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("{} GET /users/{userId}/events/{eventId}/comments. UserId={}, eventId={}, from={}, size={}",
                API_LOGSTRING, userId, eventId, from, size);
        return commentService.getCommentsForEvent(userId, eventId, size, from);
    }

    @DeleteMapping("/{commentId}")
    public void deletedComment(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @PathVariable Long commentId) {
        log.info("{} DELETE /{commentId}. UserId={}, eventId={}, commentId={}", API_LOGSTRING, userId, eventId, commentId);
        commentService.deleteCommentByUser(userId, eventId, commentId);
    }


}
