package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, Item item);

    Item patchItem(Long userId, Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(String text);
}
