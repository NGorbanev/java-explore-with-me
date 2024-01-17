package ru.practicum.ewm.comments.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {
    public Comment toComment(User user, Event event, CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .author(user)
                .event(event)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }


    public CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentShortDto> listToCommentShortDto(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        List<CommentShortDto> listDtos = new ArrayList<>();
        for (Comment comment : comments) {
            listDtos.add(toCommentShortDto(comment));
        }
        return listDtos;
    }
}
