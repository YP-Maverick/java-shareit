package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentMapper;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    //
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;


    @Transactional(readOnly = true)
    public void checkUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("NotFound. Запрос на действие с предметом от несуществующего пользователя с id {}.", userId);
            throw new NotFoundException(
                    "User with id is not exist."
            );
        }
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        checkUserId(ownerId);
        Item item = itemMapper.toItem(ownerId, itemDto);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(Long userId, Long itemId, LocalDateTime currentTime) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("NotFound. Запрос получить несуществующий предмет с id {}.", itemId);
            return new NotFoundException(
                    String.format("Item with id %d is not exist.", itemId)
            );
        });
        if (item.getOwnerId().equals(userId)) {
            return getItemDtoWithBookings(item, currentTime);
        } else {
            return itemMapper.toDto(item);
        }
    }

    @Transactional(readOnly = true)
    public ItemDto getItemDtoWithBookings(Item item, LocalDateTime currentTime) {
        List<Booking> bookings = bookingRepository.findLastAndNextForItem(item.getId(), currentTime);
        BookingItemDto lastBooking = null;
        BookingItemDto nextBooking = null;

        for (Booking booking : bookings) {
            if ((booking.getEndDate().isBefore(currentTime)
                    || (booking.getStartDate().isBefore(currentTime) && booking.getEndDate().isAfter(currentTime)))) {
                lastBooking = bookingMapper.toItemDto(booking);
            } else {
                nextBooking = bookingMapper.toItemDto(booking);
            }
        }
        return itemMapper.toDto(item, lastBooking, nextBooking);
    }

    @Override
    public ItemDto patchItem(
            Long itemId,
            Long userId,
            Map<String, Object> fields,
            LocalDateTime currentTime
    ) {
        Item item = itemRepository.findItemByOwnerId(userId, itemId).orElseThrow(() -> {
            log.error("NoAccess. Запрос пользователя с id {} на обновление предмета с id {}.", userId, itemId);
            return new NoAccessException("You haven't access to update this item.");
        });

        fields.remove("id");
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Item.class,
                    f -> f.getName().equals(k));
            field.setAccessible(true);
            ReflectionUtils.setField(field, item, v);
        });

        return getItemDtoWithBookings(itemRepository.save(item), currentTime);
    }

    @Override
    public Long deleteItem(Long userId, Long itemId) {
        log.info("Запрос удалить вещь с id {} от пользователя с id {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found.", itemId);
                    return new NoAccessException("Item not found.");
                });

        if (!item.getOwnerId().equals(userId)) {
            log.error("NoAccess. Запрос пользователя с id {} на удаление предмета с id {}.", userId, itemId);
            throw new NoAccessException("You haven't access to delete this item.");
        }

        itemRepository.deleteById(itemId);
        return itemId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByUserId(Long userId, LocalDateTime currentTime) {
        checkUserId(userId);
        List<Item> items = itemRepository.getAllByUserId(userId);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemRepository.search(text);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long itemId,
                                    Long authorId,
                                    LocalDateTime createdTime,
                                    CommentRequestDto commentRequestDto) {
        log.info("Запрос создать комментарий на вещь с id{} от пользователя с id {}", itemId, authorId);

        List<Booking> bookings = bookingRepository.findBookingToComment(authorId, itemId, createdTime);
        if (bookings.isEmpty()) {
            log.error("BadRequest. Запрос пользователя с id {} " +
                    "добавить комментарий к вещи с id {}.", authorId, itemId);
            throw new BadRequestException("You cannot leave a comment on this item.");
        }
        Comment comment = commentMapper.toComment(
                itemId,
                bookings.get(0).getBooker(),
                createdTime,
                commentRequestDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
