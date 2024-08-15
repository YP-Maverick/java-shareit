package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public void checkUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User updateUser(Long userId, Map<String, Object> fields) {

        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.error("NotFound. Запрос получить несуществующего пользователя с id {}.", userId);
                    return new NotFoundException(
                            String.format("User with id %d is not exist.", userId)
                    );
                });

        fields.remove("id");
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(User.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field, user, v);
        });
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        log.info("Запрос создать или обновить пользователя.");

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Запрос создать с используемым другим "
                    + "пользователем адресом эл. почты {}", user.getEmail());
            throw new DuplicateException("This email is already in use.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long userId) {
        log.info("Запрос получить пользователя с id {}", userId);

        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("NotFound. Запрос получить несуществующего пользователя с id {}.", userId);
            return new NotFoundException(
                    String.format("User with id %d is not exist.", userId)
            );
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }
}
