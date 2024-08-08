package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public void checkUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDto save(UserDto userDto) {
        log.info("Запрос создать или обновить пользователя.");

        try {
            User user = userRepository.save(userMapper.toUser(userDto));
            return userMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Запрос создать или обновить пользователя с используемым другим "
                    + "пользователем адресом эл. почты {}", userDto.getEmail());
            throw new DuplicateException("This email is already in use.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрос получить пользователя с id {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("NotFound. Запрос получить несуществующего пользователя с id {}.", id);
            return new NotFoundException(
                    String.format("User with id %d is not exist.", id)
            );
        });
        return userMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }
}
