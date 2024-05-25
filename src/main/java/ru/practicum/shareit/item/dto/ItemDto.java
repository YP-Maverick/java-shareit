package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class ItemDto {


    @With
    Long id; // Уникальный идентификатор вещи;

    @NotBlank(message = "Имя предмета не должно быть пустым")
    String name; // Краткое название

    @NotBlank(message = "Описание предмета не должно быть пустым")
    String description; // Развёрнутое описание;

    @NotNull(message = "Статус доступности предмета должен быть определен")
    Boolean available; // Доступна или нет вещь для аренды;
}
