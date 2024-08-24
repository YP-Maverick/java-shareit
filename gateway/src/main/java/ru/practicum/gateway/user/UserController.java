package ru.practicum.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Valid @RequestBody UserDto userDto
    ) {
        log.info("POST request from Gateway /users, createUser");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive Long userId,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH request from Gateway /users/{id}, updateUser");
        return userClient.updateUser(userId, fields);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(
            @Valid @PathVariable Long userId
    ) {
        log.info("GET request from Gateway /users/{userId}, getUserById with userId : {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET request from Gateway /users, getAllUsers");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @Valid @PathVariable Long userId
    ) {
        log.info("DELETE request from Gateway /users/{userId}, deleteUser with userId: {}", userId);
        return userClient.deleteUserById(userId);
    }
}
