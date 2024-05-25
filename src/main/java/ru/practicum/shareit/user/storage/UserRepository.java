package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User patch(Long id, User user);

    void delete(Long id);

    User getUserById(Long id);

    List<User> findAllUsers();

    boolean contains(Long id);
}
