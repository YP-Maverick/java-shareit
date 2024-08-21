package ru.practicum.shareit.user.dto;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    String name;

    @Email(message = "Электронная почта должна иметь формат адреса электронной почты")
    @NotBlank(message = "Электронная почта не должна быть пустой")
    String email;
}

