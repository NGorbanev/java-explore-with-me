package ru.practicum;

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
