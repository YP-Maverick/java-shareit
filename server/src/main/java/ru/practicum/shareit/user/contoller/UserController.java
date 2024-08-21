package ru.practicum.shareit.user.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid
            @RequestBody UserDto userDto
    ) {
        log.info("POST request /users, createUser");
        User createdUser = userService.createUser(userMapper.toUser(userDto));
        return new ResponseEntity<>(
                userMapper.toUserDto(createdUser),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable
            @Positive Long userId,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH request /users/{id}, updateUser");
        User updatedUser = userService.updateUser(userId, fields);
        return new ResponseEntity<>(
                userMapper.toUserDto(updatedUser),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @Valid
            @PathVariable Long userId
    ) {
        log.info("GET request /users/{userId}, getUserById with userId : {}",
                userId);
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(
                userMapper.toUserDto(user),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET request /users, getAllUsers");
        List<User> userDtoList = userService.getAllUsers();
        return new ResponseEntity<>(
                userDtoList.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Valid @PathVariable
            Long userId
    ) {
        log.info("DELETE request /users/{userId}, deleteUser with userId: {}",
                userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
