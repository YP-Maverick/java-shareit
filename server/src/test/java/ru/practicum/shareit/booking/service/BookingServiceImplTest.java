package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    private User user;
    private Item item;
    private Booking booking;
    private BookingRequest bookingRequest;

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

        bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .startDate(bookingRequest.getStart())
                .endDate(bookingRequest.getEnd())
                .build();
    }

    @DisplayName("Создание нового бронирования")
    @Test
    public void shouldCreateNewBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.of(item));

        item.setOwnerId(2L); // изменим ID владельца, чтобы текущий пользователь не был владельцем

        when(bookingMapper.toBooking(bookingRequest, user, item)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createdBooking = bookingService.createBooking(user.getId(), bookingRequest);

        assertNotNull(createdBooking);
        assertEquals(booking.getId(), createdBooking.getId());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }


    @DisplayName("Ошибка при создании бронирования, если item не найден")
    @Test
    public void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(user.getId(), bookingRequest);
        });

        assertEquals("Item with id 1 does not exist.", exception.getMessage());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @DisplayName("Ошибка при создании бронирования, если пользователь является владельцем предмета")
    @Test
    public void shouldThrowNotFoundExceptionWhenOwnerTriesToBookOwnItem() {
        item.setOwnerId(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(user.getId(), bookingRequest);
        });

        assertEquals("Item cannot be reserved.", exception.getMessage());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }



    @DisplayName("Получение бронирования по ID")
    @Test
    public void shouldGetBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @DisplayName("Ошибка при получении бронирования по несуществующему ID")
    @Test
    public void shouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(user.getId(), 99L);
        });

        assertEquals("Booking with id 99 does not exist.", exception.getMessage());
        verify(bookingRepository, times(1)).findById(99L);
    }

    @DisplayName("Обновление статуса бронирования")
    @Test
    public void shouldUpdateBookingStatus() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking updatedBooking = bookingService.updateBookingStatus(user.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @DisplayName("Ошибка при обновлении статуса бронирования не с владельца предмета")
    @Test
    public void shouldThrowBadRequestExceptionWhenRequesterNotOwner() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            bookingService.updateBookingStatus(2L, booking.getId(), true);
        });

        assertEquals("You don't have access to update this booking.", exception.getMessage());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }
}
