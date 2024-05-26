package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto patchItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}
