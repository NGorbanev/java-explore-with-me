package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000, message = "Annotation length should be from 20 to 2000 digits.")
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotNull
    @NotBlank
    @Size(min = 20, max = 7000, message = "Description length should be from 20 to 7000 digits")
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120, message = "Title lenght should be from 3 to 120 digits.")
    private String title;
}
