package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("POST Item request with userId: {}", userId);
        ItemDto createdItem = itemService.createItem(userId, itemDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long ownerId,
            @Positive(message = "Item's id should be positive") @PathVariable Long itemId,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH Item request with ownerId {} for itemId: {}", ownerId, itemId);
        ItemDto updatedItem = itemService.patchItem(itemId, ownerId, fields, LocalDateTime.now());
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET Item request with userId {} for itemId: {}", userId, itemId);
        ItemDto item = itemService.getItemById(userId, itemId, LocalDateTime.now());
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET all Items request by userId: {}", userId);
        List<ItemDto> itemDtoList = itemService.getAllItemsByUserId(userId, LocalDateTime.now());
        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("GET items request. Search items with text: {}", text);
        List<ItemDto> itemDtoList = itemService.searchItems(text);
        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User's id should be positive") Long authorId,
            @RequestBody @Valid CommentRequestDto commentRequestDto
    ) {
        log.info("POST comment for itemId: {} by authorId: {}", itemId, authorId);
        CommentDto createdComment = itemService.createComment(itemId, authorId, LocalDateTime.now(), commentRequestDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
}
