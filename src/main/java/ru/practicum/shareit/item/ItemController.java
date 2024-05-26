package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        ItemDto createdItem = itemService.createItem(userId, itemDto);
        log.info("Received POST request createItem with userId: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.patchItem(userId, itemId, itemDto);
        log.info("Received PATCH request patchItem for itemId: {}", itemId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@Valid @PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        log.info("Received GET request for item with itemId: {}", itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@Valid @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> itemDtoList = itemService.getAllItemsByUserId(userId);
        log.info("Received GET request getAllItems for userId: {}", userId);
        return ResponseEntity.ok(itemDtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        List<ItemDto> itemDtoList = itemService.searchItems(text);
        log.info("Received GET request searchItems with text: {}", text);
        return ResponseEntity.ok(itemDtoList);
    }
}
