package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public Item createItem(Long userId, Item item) {
        if (!userRepository.contains(userId)) {
            throw new NotFoundException("User not found");
        }
        return itemRepository.create(userId, item);
    }

    @Override
    public Item patchItem(Long userId, Long itemId, Item item) {
        if (!itemRepository.contains(itemId)) {
            throw new NotFoundException("Item not found");
        }

        if (!userRepository.contains(userId)) {
            throw new NotFoundException("User not found");
        }
        return itemRepository.patch(userId, itemId, item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemRepository.getAllByUserId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.search(text);
    }
}
