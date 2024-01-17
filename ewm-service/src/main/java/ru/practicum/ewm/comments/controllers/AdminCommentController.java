package ru.practicum.ewm.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comments.service.CommentsService;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/admin/events/comments")
public class AdminCommentController {
    private final CommentsService commentService;

    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("{} DELETE /{commentId} commentId={}", API_LOGSTRING, commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
