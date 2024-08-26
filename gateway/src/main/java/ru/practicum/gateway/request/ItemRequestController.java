package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.CreateItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CreateItemRequestDto creationDto) {
        log.info("POST Item Request from Gateway with userId: {}", userId);
        return itemRequestClient.createItemRequest(userId, creationDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("GET Item Request with requestId: {} by userId: {}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET Item Requests from Gateway by owner userId: {}", userId);
        return itemRequestClient.getItemRequestByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam int from,
            @RequestParam int size) {
        log.info("GET all Item Requests from Gateway by userId: {} from: {} size: {}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}
