package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.IncomingUserDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;

    IncomingUserDto incomingUserDto = IncomingUserDto.builder()
            .name("Ivan")
            .email("ivan@ivanov.net")
            .build();

    @Test
    public void ShouldCreateUser() {
        UserDto userDto = userService.addUser(incomingUserDto);
        Assertions.assertEquals("Ivan", userDto.getName());
        Assertions.assertEquals("ivan@ivanov.net", userDto.getEmail());
        Assertions.assertTrue(userDto.getId() != 0);
    }

    @Test
    public void ShouldReturnUserDtoAndUserDtosList() {
        userService.addUser(incomingUserDto);
        UserDto result = userService.findUserDtos(null, 0, 10).get(0);
        Assertions.assertEquals(incomingUserDto.getName(), result.getName());
        Assertions.assertEquals(incomingUserDto.getEmail(), result.getEmail());
        List<Long> usersIdList = List.of(result.getId());
        Assertions.assertEquals(1, userService.findUserDtos(usersIdList, 0, 10).size());
    }

    @Test
    public void ShouldDeleteUser() {
        userService.addUser(incomingUserDto);
        UserDto result = userService.findUserDtos(null, 0, 10).get(0);
        List<Long> usersIdList = List.of(result.getId());
        Assertions.assertEquals(1, userService.findUserDtos(usersIdList, 0, 10).size());
        userService.deleteUserById(usersIdList.get(0));
        Assertions.assertEquals(0, userService.findUserDtos(usersIdList, 0, 10).size());
    }
}
