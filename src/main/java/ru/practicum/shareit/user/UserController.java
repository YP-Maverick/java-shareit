package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        log.info("Получен POST запрос create");
        return new ResponseEntity<>(UserMapper.toUserDto(createdUser), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> patchUserById(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User patchedUser = userService.patchUserById(userId, UserMapper.toUser(userDto));
        log.info("Получен PATCH запрос patchUserById с userId: {}", userId);
        return new ResponseEntity<>(UserMapper.toUserDto(patchedUser), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@Valid @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        log.info("Получен GET запрос getUserById с userId: {}", userId);
        return new ResponseEntity<>(UserMapper.toUserDto(user), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        List<UserDto> userDtoList = userService.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Получен GET запрос getAll");
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Получен DELETE запрос deleteUser с userId: {}", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
