package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional(readOnly = true)
    public void checkUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("NotFound. Request for action on item from a non-existent user with id {}.", userId);
            throw new NotFoundException(
                    "User with id does not exist."
            );
        }
    }

    @Override
    public Item createItem(Long ownerId, Item item) {
        log.info("Request to create an item from user with id {}", ownerId);

        checkUserId(ownerId);
        if (item.getRequestId() != null) {
            if (!itemRequestRepository.existsById(item.getRequestId())) {
                log.error("NotFound. Request to create an item in response to a non-existent request with id {}.",
                        item.getRequestId());
                throw new NotFoundException(
                        String.format("ItemRequest with id %d does not exist.", item.getRequestId()));
            }
        }
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItemById(Long userId, Long itemId, LocalDateTime currentTime) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("NotFound. Request to get a non-existent item with id {}.", itemId);
            return new NotFoundException(
                    String.format("Item with id %d does not exist.", itemId)
            );
        });
    }

    public BookingInfo getBookingsInfoForOwner(Long userId, Item item, LocalDateTime currentTime) {
        if (!item.getOwnerId().equals(userId)) return null;

        return BookingInfo.builder()
                .lastBooking(bookingRepository.findLastBookings(item.getId(), currentTime))
                .nextBooking(bookingRepository.findNextBookings(item.getId(), currentTime))
                .build();
    }

    @Override
    public Item patchItem(
            Long itemId,
            Long userId,
            Map<String, Object> fields
    ) {
        Item item = itemRepository.findItemByOwnerId(userId, itemId).orElseThrow(() -> {
            log.error("NoAccess. Request from user with id {} to update item with id {}.", userId, itemId);
            return new NoAccessException("You do not have access to update this item.");
        });

        fields.remove("id");
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Item.class, f -> f.getName().equals(k));
            field.setAccessible(true);
            ReflectionUtils.setField(field, item, v);
        });

        return itemRepository.save(item);
    }

    @Override
    public Long deleteItem(Long userId, Long itemId) {
        log.info("Request to delete item with id {} from user with id {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found.", itemId);
                    return new NoAccessException("Item not found.");
                });

        if (!item.getOwnerId().equals(userId)) {
            log.error("NoAccess. Request from user with id {} to delete item with id {}.", userId, itemId);
            throw new NoAccessException("You do not have access to delete this item.");
        }

        itemRepository.deleteById(itemId);
        return itemId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAllItemsByUserId(Long userId, LocalDateTime currentTime) {
        checkUserId(userId);
        return itemRepository.getAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) return new ArrayList<>();
        return itemRepository.search(text);
    }

    @Override
    public Comment createComment(Long itemId,
                                 Long authorId,
                                 LocalDateTime createdTime,
                                 CommentRequestDto commentRequestDto) {
        log.info("Request to create a comment on item with id {} from user with id {}", itemId, authorId);

        Booking booking = bookingRepository.findBookingToComment(authorId, itemId, createdTime);

        // TODO What is happening here?
        if (booking == null) {
            log.error("BadRequest. Request from user with id {} " +
                    "to add a comment to item with id {}.", authorId, itemId);
            throw new BadRequestException("You cannot leave a comment on this item.");
        }

        User author = userRepository.findById(authorId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Comment comment = Comment.builder()
                .itemId(itemId)
                .text(commentRequestDto.getText())
                .itemId(itemId)
                .author(author)
                .created(createdTime)
                .build();
        return commentRepository.save(comment);
    }
}
