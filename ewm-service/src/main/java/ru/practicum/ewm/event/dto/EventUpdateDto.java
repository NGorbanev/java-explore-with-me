package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {
    @Size(min = 20, max = 2000, message = "Annotation length should be from 20 to 2000 digs")
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000, message = "Description length should be from 20 to 7000 digs")
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120, message = "Title length should be from 3 to 120")
    private String title;
}
