package ru.practicum.ewm;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatsDto {
    private String app;
    private String uri;
    private Long hits;
}
