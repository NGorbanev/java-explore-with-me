package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.ShortUserDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortEventDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private Long id;
    private ShortUserDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
