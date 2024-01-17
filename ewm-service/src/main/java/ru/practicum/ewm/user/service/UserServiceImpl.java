package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.user.dto.IncomingUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.utils.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository storage;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUserDtos(List<Long> ids, Integer from, Integer size) {
        log.info("Find users request processing. Range of ids: {}", ids);
        List<User> users;
        Pageable pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = storage.findAll(pageRequest).getContent();
        } else {
            users = storage.findByIdIn(ids, pageRequest);
        }
        return UserMapper.toUserDtos(users);
    }

    @Override
    public UserDto addUser(IncomingUserDto incomingUserDto) {
        User user = UserMapper.toUser(incomingUserDto);
        log.info("Adding user: {}", incomingUserDto);
        return UserMapper.toUserDto(storage.save(user));
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Deleting userId={} is servicing", userId);
        storage.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return storage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%s not found", userId)));
    }
}
