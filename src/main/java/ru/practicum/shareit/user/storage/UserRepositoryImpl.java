package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> storage = new HashMap<>();
    private static Long idCounter = 1L;

    public static Long generateId() {
        return idCounter++;
    }

    @Override
    public User create(User user) {
        String email = user.getEmail();
        if (!isEmailUnique(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        Long generatedId = generateId();
        User userWithId = user.withId(generatedId);
        storage.put(generatedId, userWithId);
        return userWithId;
    }

    @Override
    public User patch(Long id, User user) {

        // Обновляем только те поля, которые не null
        User existingUser = storage.get(id);

        String name = user.getName() != null ? user.getName() : existingUser.getName();
        String email = user.getEmail() != null ? user.getEmail() : existingUser.getEmail();

        // Проверяем уникальность email только если он был изменен
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (!isEmailUnique(user.getEmail())) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }

        User updatedUser = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        storage.put(id, updatedUser);
        return updatedUser;
    }

    private boolean isEmailUnique(String email) {
        return storage.values().stream().noneMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public User getUserById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean contains(Long id) {
        return storage.containsKey(id);
    }
}

