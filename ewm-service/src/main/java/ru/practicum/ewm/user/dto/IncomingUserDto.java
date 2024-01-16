package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomingUserDto {
    @Email
    @NotNull
    @Size(min = 6, max = 254, message = "Email length should be from 6 to 254 digits")
    private String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "Name length should be from 2 to 250 digits")
    private String name;
}
