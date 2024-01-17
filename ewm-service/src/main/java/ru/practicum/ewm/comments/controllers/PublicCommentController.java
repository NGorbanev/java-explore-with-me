package ru.practicum.ewm.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.service.CommentsService;

import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/comments")
public class PublicCommentController {
    private final CommentsService commentService;

    @GetMapping
    public List<CommentShortDto> getCommentsForEvent(@PathVariable Long eventId,
                                                     @RequestParam(required = false, defaultValue = "0")
                                                     int from,
                                                     @RequestParam(required = false, defaultValue = "10")
                                                     int size) {
        log.info("{} GET /comments. EventId={}, from={}, size={}", API_LOGSTRING, eventId, from, size);
        return commentService.getCommentsForEvent(eventId, size, from);
    }
}
