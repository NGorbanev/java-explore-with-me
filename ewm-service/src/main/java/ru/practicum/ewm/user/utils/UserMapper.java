package ru.practicum.ewm.user.utils;

import ru.practicum.ewm.user.dto.IncomingUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User toUser(IncomingUserDto inDto) {
        User user = new User();
        user.setEmail(inDto.getEmail());
        user.setId(user.getId());
        user.setName(inDto.getName());
        return user;
    }

    public static UserDto toUserOutDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserDto> toOutDtos(List<User> users) {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserOutDto(user));
        }
        return dtos;
    }

    public static ShortUserDto toUserShortDto(User user) {
        return new ShortUserDto(user.getId(), user.getName());
    }
}
