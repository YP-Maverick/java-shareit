package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    Booking createBooking(Long bookerId, BookingRequest bookingRequest);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllByOwnerId(Long ownerId, String state, LocalDateTime currentTime);

    List<Booking> getAllByBookerId(Long bookerId, String state, LocalDateTime currentTime);

    Booking updateBookingStatus(Long ownerId, Long bookingId, Boolean approved);

    Long deleteBooking(Long bookerId, Long bookingId);

}

