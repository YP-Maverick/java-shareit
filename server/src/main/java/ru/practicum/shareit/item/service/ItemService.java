package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ItemService {

    Item createItem(Long userId, Item item);

    Item getItemById(Long userId, Long itemId, LocalDateTime currentTime);

    BookingInfo getBookingsInfoForOwner(Long userId, Item item, LocalDateTime currentTime);

    List<Item> getAllItemsByUserId(Long userId, LocalDateTime currentTime);

    Item patchItem(Long itemId, Long userId, Map<String, Object> fields);

    Long deleteItem(Long userId, Long itemId);

    List<Item> searchItems(String text);

    Comment createComment(Long itemId,
                          Long authorId,
                          LocalDateTime createdTime,
                          CommentRequestDto commentRequestDto);

}
