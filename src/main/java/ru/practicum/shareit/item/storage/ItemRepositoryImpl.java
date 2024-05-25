package ru.practicum.shareit.item.storage;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.exception.NotOwnerException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> storage = new HashMap<>();
    private static Long idCounter = 1L;

    public static Long generateId() {
        return idCounter++;
    }

    @Override
    public Item create(Long userId, Item item) {
        Long itemId = generateId();
        Item itemWithId = Item.builder()
                .id(itemId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(userId)
                .build();
        storage.put(itemId, itemWithId);
        return itemWithId;
    }

    @Override
    public Item patch(Long userId, Long itemId, Item item) {
        Item existingItem = storage.get(itemId);

        if (!existingItem.getOwnerId().equals(userId)) {
            throw new NotOwnerException("Данный предмет принадлежит другому пользователю");
        }

        // Обновляем только те поля, которые не null
        String name = item.getName() != null ? item.getName() : existingItem.getName();
        String description = item.getDescription() != null ? item.getDescription() : existingItem.getDescription();
        Boolean available = item.getAvailable() != null ? item.getAvailable() : existingItem.getAvailable();

        Item updatedItem = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(existingItem.getOwnerId())
                .build();

        storage.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public Item getById(Long itemId) {
        return storage.get(itemId);
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        // Предположим, что есть дополнительная логика для фильтрации предметов по userId
        return storage.values().stream().filter(item -> userId.equals(item.getOwnerId())).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase(Locale.ROOT);

        return storage.values()
                .stream()
                .filter(item -> containsStringIgnoreCase(item.getName(), searchText)
                             || containsStringIgnoreCase(item.getDescription(), searchText)
                             && item.getAvailable())
                .collect(Collectors.toList());
    }

    private boolean containsStringIgnoreCase(String source, String target) {
        return source.toLowerCase(Locale.ROOT).contains(target);
    }

    @Override
    public boolean contains(Long id) {
        return storage.containsKey(id);
    }
}
