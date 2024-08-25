package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @DisplayName("Создание нового пользователя")
    @Test
    public void shouldCreateNewUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());

        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Обработка ошибки при создании дубликата пользователя")
    @Test
    public void shouldThrowDuplicateExceptionWhenCreatingDuplicateUser() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        Exception exception = assertThrows(DuplicateException.class, () -> userService.createUser(user));
        assertEquals("This email is already in use.", exception.getMessage());

        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Получение пользователя по ID")
    @Test
    public void shouldGetUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());

        verify(userRepository, times(1)).findById(1L);
    }

    @DisplayName("Получение несуществующего пользователя по ID")
    @Test
    public void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
        assertEquals("User with id 2 does not exist.", exception.getMessage());

        verify(userRepository, times(1)).findById(2L);
    }

    @DisplayName("Удаление пользователя")
    @Test
    public void shouldDeleteUser() {
        doNothing().when(userRepository).delete(anyLong());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(1L);
    }

    @DisplayName("Получение всех пользователей")
    @Test
    public void shouldReturnAllUsers() {
        User user2 = User.builder().id(2L).name("Test User 2").email("test2@example.com").build();
        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());

        verify(userRepository, times(1)).findAll();
    }
}
