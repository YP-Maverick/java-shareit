package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto getItemById(Long userId, Long itemId, LocalDateTime currentTime);

    ItemDto patchItem(Long itemId, Long userId, Map<String, Object> fields, LocalDateTime currentTime);

    Long deleteItem(Long userId, Long itemId);

    List<ItemDto> getAllItemsByUserId(Long userId, LocalDateTime currentTime);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(Long itemId,
                             Long authorId,
                             LocalDateTime createdTime,
                             CommentRequestDto commentRequestDto);
}
