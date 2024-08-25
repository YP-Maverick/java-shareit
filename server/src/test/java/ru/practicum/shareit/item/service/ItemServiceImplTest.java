package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .ownerId(user.getId())
                .available(true)
                .build();
    }

    @DisplayName("Создание нового предмета")
    @Test
    public void shouldCreateNewItem() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(user.getId(), item);
        assertNotNull(createdItem);
        assertEquals(item.getName(), createdItem.getName());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @DisplayName("Ошибка при создании предмета, если пользователь не существует")
    @Test
    public void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.createItem(user.getId(), item);
        });
        assertEquals("User with id does not exist.", exception.getMessage());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @DisplayName("Получение предмета по ID")
    @Test
    public void shouldGetItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item foundItem = itemService.getItemById(user.getId(), item.getId(), LocalDateTime.now());
        assertNotNull(foundItem);
        assertEquals(item.getId(), foundItem.getId());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @DisplayName("Ошибка получения предмета по несуществующему ID")
    @Test
    public void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(user.getId(), 99L, LocalDateTime.now());
        });
        assertEquals("Item with id 99 does not exist.", exception.getMessage());

        verify(itemRepository, times(1)).findById(99L);
    }

    @DisplayName("Обновление предмета")
    @Test
    public void shouldUpdateItem() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Item");

        when(itemRepository.findItemByOwnerId(user.getId(), item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = itemService.patchItem(item.getId(), user.getId(), updates);
        assertNotNull(updatedItem);
        assertEquals("Updated Item", updatedItem.getName());

        verify(itemRepository, times(1)).findItemByOwnerId(user.getId(), item.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @DisplayName("Ошибка при обновлении предмета, если предмет не принадлежит пользователю")
    @Test
    public void shouldThrowNoAccessExceptionWhenItemNotOwned() {
        when(itemRepository.findItemByOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoAccessException.class, () -> {
            itemService.patchItem(item.getId(), 2L, new HashMap<>());
        });
        assertEquals("You do not have access to update this item.", exception.getMessage());

        verify(itemRepository, times(1)).findItemByOwnerId(2L, item.getId());
    }

    @DisplayName("Ошибка удаления предмета, если предмет не существует")
    @Test
    public void shouldThrowNoAccessExceptionWhenItemNotFoundOnDelete() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoAccessException.class, () -> {
            itemService.deleteItem(user.getId(), item.getId());
        });
        assertEquals("Item not found.", exception.getMessage());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @DisplayName("Создание комментария")
    @Test
    public void shouldCreateComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test Comment");

        Booking booking = Booking.builder().id(1L).item(item).booker(user).build();
        when(bookingRepository.findBookingToComment(anyLong(), anyLong(), any())).thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        Comment comment = itemService.createComment(item.getId(), user.getId(), LocalDateTime.now(), commentRequestDto);
        assertNotNull(comment);

        verify(bookingRepository, times(1)).findBookingToComment(anyLong(), anyLong(), any());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("Ошибка при создании комментария, если нет брони")
    @Test
    public void shouldThrowBadRequestExceptionWhenNoBooking() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test Comment");

        when(bookingRepository.findBookingToComment(anyLong(), anyLong(), any())).thenReturn(null);

        Exception exception = assertThrows(BadRequestException.class, () -> {
            itemService.createComment(item.getId(), user.getId(), LocalDateTime.now(), commentRequestDto);
        });
        assertEquals("You cannot leave a comment on this item.", exception.getMessage());

        verify(bookingRepository, times(1)).findBookingToComment(anyLong(), anyLong(), any());
    }
}


