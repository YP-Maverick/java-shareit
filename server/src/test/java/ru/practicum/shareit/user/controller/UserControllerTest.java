package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.contoller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@email.ru")
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("user@email.ru")
                .build();
    }

    private MockHttpServletResponse createUserResponse(UserDto userDto) throws Exception {
        return mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Добавить пользователя")
    @Test
    public void shouldCreateUser() throws Exception {
        UserDto userDto = createUserDto();
        User createdUser = createUser();

        when(userMapper.toUser(userDto)).thenReturn(createdUser);
        when(userService.createUser(any(User.class))).thenReturn(createdUser);
        when(userMapper.toUserDto(createdUser)).thenReturn(userDto);

        MockHttpServletResponse response = createUserResponse(userDto);

        assertEquals(201, response.getStatus());
        assertEquals(mapper.writeValueAsString(userDto), response.getContentAsString());
        verify(userService, times(1)).createUser(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    private MockHttpServletResponse updateUserResponse(Long userId, Map<String, Object> updatedFields) throws Exception {
        return mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(updatedFields))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Обновить пользователя")
    @Test
    public void shouldUpdateUser() throws Exception {
        UserDto userDto = createUserDto();
        User updatedUser = createUser(); // Напоминаем, что это обновленная версия пользователя
        UserDto expectedUpdatedDto = userDto.withName("updated").withEmail("updated@email");

        Map<String, Object> updatedFields = Map.of(
                "name", "updated",
                "email", "updated@email"
        );

        when(userService.updateUser(anyLong(), any(Map.class))).thenReturn(updatedUser); // Логика мока
        when(userMapper.toUserDto(any(User.class))).thenReturn(expectedUpdatedDto); // Логика мока возвращает обновленный DTO

        MockHttpServletResponse response = updateUserResponse(userDto.getId(), updatedFields);

        assertEquals(200, response.getStatus());
        assertEquals(mapper.writeValueAsString(expectedUpdatedDto), response.getContentAsString());
        verify(userService, times(1)).updateUser(userDto.getId(), updatedFields);
        verifyNoMoreInteractions(userService);
    }

    @DisplayName("Получить всех пользователей")
    @Test
    public void shouldGetAllUsers() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.getAllUsers()).thenReturn(List.of(createUser())); // Исправлено
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto); // Мок, чтобы вернуть правильный DTO

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto))));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    private MockHttpServletResponse getUserResponse(Long userId) throws Exception {
        return mvc.perform(get("/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Получить пользователя по id")
    @Test
    public void shouldGetUserById() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.getUserById(userDto.getId())).thenReturn(createUser()); // Исправлено
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto); // Логика мока

        MockHttpServletResponse response = getUserResponse(userDto.getId());

        assertEquals(200, response.getStatus());
        assertEquals(mapper.writeValueAsString(userDto), response.getContentAsString());
        verify(userService, times(1)).getUserById(anyLong());
        verifyNoMoreInteractions(userService);
    }

    private MockHttpServletResponse deleteUserResponse(Long userId) throws Exception {
        return mvc.perform(delete("/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Удалить пользователя")
    @Test
    public void shouldDeleteUser() throws Exception {
        UserDto userDto = createUserDto();

        doNothing().when(userService).deleteUser(anyLong());

        MockHttpServletResponse response = deleteUserResponse(userDto.getId());

        assertEquals(200, response.getStatus());
        verify(userService, times(1)).deleteUser(anyLong());
        verifyNoMoreInteractions(userService);
    }
}
