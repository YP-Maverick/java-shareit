package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        Item createdItem = itemService.createItem(userId, ItemMapper.toItem(itemDto));
        log.info("Received GET request createItem with userId: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemMapper.toItemDto(createdItem));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        Item updatedItem = itemService.patchItem(userId, itemId, ItemMapper.toItem(itemDto));
        log.info("Received PATCH request patchItem for itemId: {}", itemId);
        return ResponseEntity.ok(ItemMapper.toItemDto(updatedItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@Valid @PathVariable Long itemId) {
        Item item = itemService.getItemById(itemId);
        log.info("Received GET request for item with itemId: {}", itemId);
        return ResponseEntity.ok(ItemMapper.toItemDto(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@Valid @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = itemService.getAllItems(userId);
        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Received GET request getAllItems for userId: {}", userId);
        return ResponseEntity.ok(itemDtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {

        List<Item> items = itemService.searchItems(text);
        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Received GET request searchItems with text: {}", text);
        return ResponseEntity.ok(itemDtoList);
    }
}
