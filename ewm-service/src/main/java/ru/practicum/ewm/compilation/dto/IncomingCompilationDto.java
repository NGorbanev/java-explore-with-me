package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingCompilationDto {
    private List<Long> events;
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "Compilation title length should be from 1 to 50 digits")
    private String title;
}
