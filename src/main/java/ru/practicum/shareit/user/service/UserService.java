package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User createUser(User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUser(Long userId, Map<String, Object> fields);

    void deleteUser(Long userId);

    void checkUserId(Long userId);
}
