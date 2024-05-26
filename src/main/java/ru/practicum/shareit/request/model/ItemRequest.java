package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
@Builder
public class ItemRequest {
    Long id; // Уникальный идентификатор запроса;
    String description; // Текст запроса, содержащий описание требуемой вещи;
    User requestor; // Пользователь, создавший запрос;
    LocalDateTime creationTime; // Дата и время создания запроса.
}
