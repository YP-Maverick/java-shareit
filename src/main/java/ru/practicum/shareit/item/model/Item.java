package ru.practicum.shareit.item.model;


import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class Item {

    @With
    Long id; // Уникальный идентификатор вещи;

    @NotBlank(message = "Имя предмета не должно быть пустым")
    String name; // Краткое название

    @NotBlank(message = "Описание предмета не должно быть пустым")
    String description; // Развёрнутое описание;

    @NotNull(message = "Статус доступности предмета должен быть определен")
    Boolean available; // Доступна или нет вещь для аренды;

    Long ownerId; // Владелец вещи

    ItemRequest request; // Запрос, если вещь была создана по нему
}
