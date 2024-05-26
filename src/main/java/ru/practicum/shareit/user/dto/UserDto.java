package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder
public class UserDto {

    Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    String name;

    @Email(message = "Электронная почта должна иметь формат адреса электронной почты")
    @NotBlank(message = "Электронная почта не должна быть пустой")
    String email;
}

