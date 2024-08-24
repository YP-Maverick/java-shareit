package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {

    ItemRequest create(Long userId, LocalDateTime creationDate, CreateItemRequestDto creationDto);

    ItemRequest getById(Long userId, Long requestId);

    List<ItemRequest> getByOwnerId(Long userId);

    List<ItemRequest> getAll(Long userId, int from, int size);
}