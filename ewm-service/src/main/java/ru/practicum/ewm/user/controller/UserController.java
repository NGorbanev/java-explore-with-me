package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.IncomingUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("{} GET /admin/users. From={}, Size={}", API_LOGSTRING, from, size);
        return userService.findUserDtos(ids, from, size);
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull @PathVariable Long userId) {
        log.info("{} DELETE /admin/users/{userId} userId={}", API_LOGSTRING, userId);
        userService.deleteUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody IncomingUserDto incomingUserDto) {
        log.info("{} POST /admin/users incomingUserDto={}", API_LOGSTRING, incomingUserDto.toString());
        return userService.addUser(incomingUserDto);
    }
}
