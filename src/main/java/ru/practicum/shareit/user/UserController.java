package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        log.info("Received POST request createUser");
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> patchUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto patchedUser = userService.patchUser(userId, userDto);
        log.info("Received PATCH request patchUser with userId: {}", userId);
        return new ResponseEntity<>(patchedUser, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@Valid @PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        log.info("Received GET request getUserById with userId: {}", userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userService.getAllUsers();
        log.info("Received GET request getUserById.");
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Received DELETE request to delete user with userId: {}", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
