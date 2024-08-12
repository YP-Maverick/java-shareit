package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto);

    BookingDto updateBookingStatus(Long ownerId, Long bookingId, Boolean approved);

    Long deleteBooking(Long bookerId, Long bookingId);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> findAllByOwnerId(Long ownerId, String state, LocalDateTime currentTime);

    List<BookingDto> findAllByBookerId(Long bookerId, String state, LocalDateTime currentTime);
}

