package ru.practicum.shareit.user.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserDto userDto
    ) {
        log.info("POST request /users, createUser");
        UserDto createdUser = userService.save(userDto);
        return new ResponseEntity<>(createdUser,
                HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH request /users/{id}, updateUser");
        UserDto userDto = userService.getUserById(id);
        fields.remove("id");
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(UserDto.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field, userDto, v);
        });
        return new ResponseEntity<>(userService.save(userDto),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @Valid @PathVariable
            Long userId
    ) {
        log.info("GET request /users/{userId}, getUserById with userId : {}", userId);
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user,
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET request /users, getAllUsers");
        List<UserDto> userDtoList = userService.getAllUsers();
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Valid @PathVariable
            Long userId
    ) {
        log.info("DELETE request /users/{userId}, deleteUser with userId: {}", userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
