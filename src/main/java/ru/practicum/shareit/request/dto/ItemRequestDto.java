package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDto {
    Long id; // Уникальный идентификатор запроса;
    String description; // Текст запроса, содержащий описание требуемой вещи;
    User requestor; // Пользователь, создавший запрос;
    LocalDateTime creationTime; // Дата и время создания запроса.
}
