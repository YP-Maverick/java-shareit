package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.comment.dto.CommentRequestDto;
import ru.practicum.gateway.item.dto.ItemDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId,
            @Valid
            @RequestBody ItemDto itemDto
    ) {
        log.info("POST Item request from Gateway with userId: {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long ownerId,
            @Positive(message = "Item's id should be positive") @PathVariable Long itemId,
            @RequestBody Map<String, Object> fields
    ) {
        log.info("PATCH Item request from Gateway with ownerId {} for itemId: {}", ownerId, itemId);
        return itemClient.patchItem(ownerId, itemId, fields);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET Item request from Gateway with userId {} for itemId: {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId
    ) {
        log.info("GET all Items request from Gateway by userId: {}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam("text") String text
    ) {
        log.info("GET items request from Gateway. Search items with text: {}", text);
        if (text.isBlank() || text.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable @Positive(message = "Item's id should be positive") Long itemId,
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long authorId,
            @RequestBody @Valid CommentRequestDto commentRequestDto
    ) {
        log.info("POST comment request from Gateway for itemId: {} by authorId: {}", itemId, authorId);
        return itemClient.createComment(itemId, authorId, commentRequestDto);
    }
}
