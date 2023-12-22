package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.IncomingUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto addUser(IncomingUserDto inDto);

    void deleteUserById(Long userId);

    User findUserById(Long userId);
}
