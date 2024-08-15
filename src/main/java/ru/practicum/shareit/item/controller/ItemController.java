package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId,
            @Valid
            @RequestBody ItemDto itemDto
    ) {
        log.info("POST Item request with userId: {}", userId);
        Item createdItem = itemService.createItem(userId, itemMapper.toItem(userId, itemDto));
        return new ResponseEntity<>(
                itemMapper.toDto(createdItem),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long ownerId,
            @Positive(message = "Item's id should be positive")
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH Item request with ownerId {} for itemId: {}", ownerId, itemId);
        Item updatedItem = itemService.patchItem(itemId, ownerId, fields);
        BookingInfo bookingInfo = itemService.getBookingsInfoForOwner(ownerId, updatedItem, LocalDateTime.now());

        return new ResponseEntity<>(
                itemMapper.toDto(updatedItem, bookingInfo),
                HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable
            @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET Item request with userId {} for itemId: {}", userId, itemId);
        Item item = itemService.getItemById(userId, itemId, LocalDateTime.now());
        BookingInfo bookingInfo = itemService.getBookingsInfoForOwner(userId, item, LocalDateTime.now());
        return new ResponseEntity<>(
                itemMapper.toDto(item, bookingInfo),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET all Items request by userId: {}", userId);
        List<Item> itemList = itemService.getAllItemsByUserId(userId, LocalDateTime.now());
        return new ResponseEntity<>(
                itemList.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam("text") String text
    ) {
        log.info("GET items request. Search items with text: {}", text);
        List<Item> itemList = itemService.searchItems(text);
        return new ResponseEntity<>(
                itemList.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long authorId,
            @RequestBody @Valid CommentRequestDto commentRequestDto
    ) {
        log.info("POST comment for itemId: {} by authorId: {}", itemId, authorId);
        Comment createdComment = itemService.createComment(itemId, authorId, LocalDateTime.now(), commentRequestDto);
        return new ResponseEntity<>(
                commentMapper.toCommentDto(createdComment),
                HttpStatus.CREATED);
    }
}
