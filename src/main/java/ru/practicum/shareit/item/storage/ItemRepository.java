package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Long userId, Item item);

    Item patch(Long userId, Long itemId, Item item);

    Item getById(Long itemId);

    List<Item> getAllByUserId(Long userId);

    List<Item> search(String text);

    boolean contains(Long id);
}
