package ru.practicum.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@With
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 200, message = "Name's size shouldn't be more than 200 characters")
    String name;

    @Email(message = "Электронная почта должна иметь формат адреса электронной почты")
    @NotBlank(message = "Электронная почта не должна быть пустой")
    @Size(max = 200, message = "Email's size shouldn't be more than 200 characters")
    String email;
}
