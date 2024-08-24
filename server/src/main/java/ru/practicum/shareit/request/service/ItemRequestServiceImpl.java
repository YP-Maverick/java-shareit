package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestMapper mapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("NotFound. Request for action on item from a non-existent user with id {}.", userId);
            throw new NotFoundException(
                    String.format("User with id %d does not exist.", userId)
            );
        }
    }

    @Override
    public ItemRequest create(Long userId,
                              LocalDateTime creationDate,
                              CreateItemRequestDto creationDto) {
        log.info("Creating request from user with id {}", userId);

        checkUser(userId);
        ItemRequest request = mapper.toItemRequest(userId, creationDate, creationDto);
        return itemRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequest getById(Long userId, Long requestId) {
        log.info("Retrieving request with id {} from user with id {}", requestId, userId);

        checkUser(userId);
        return itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.error("NotFound. Request to get a non-existent request with id {}.", requestId);
            return new NotFoundException(
                    String.format("ItemRequest with id %d does not exist.", requestId)
            );
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequest> getByOwnerId(Long userId) {
        log.info("Retrieving requests from user with id {}", userId);

        User owner = userRepository.findById(userId).orElseThrow(() -> {
            log.error("NotFound. Request to get a non-existent user with id {}.", userId);
            return new NotFoundException(
                    String.format("User with id %d does not exist.", userId)
            );
        });
        return owner.getRequests();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequest> getAll(Long userId, int from, int size) {
        log.info("Retrieving requests from other users for user with id {}", userId);

        checkUser(userId);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"));
        return itemRequestRepository.findByOwnerIdNot(userId, pageable);
    }
}
