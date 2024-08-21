package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long bookerId,
            @RequestBody
            @Valid BookingRequest bookingRequest
    ) {
        log.info("POST Booking request from user with userId {} and itemId {}",
                bookerId, bookingRequest.getItemId());
        Booking createdBooking = bookingService.createBooking(bookerId, bookingRequest);
        return new ResponseEntity<>(
                bookingMapper.toBookingDto(createdBooking),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long ownerId,
            @PathVariable
            @Positive(message = "Booking's id should be positive") Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info("PATCH request to change the status of booking with id {} from ownerId {}", bookingId, ownerId);
        if (approved == null) {
            log.error("BadRequest. Смена статуса бронирования с id {} на null от пользователя с id {}", bookingId, ownerId);
            throw new BadRequestException("Parameter approved should be true or false");
        }
        Booking updatedBooking = bookingService.updateBookingStatus(ownerId, bookingId, approved);
        return new ResponseEntity<>(
                bookingMapper.toBookingDto(updatedBooking),
                HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long bookerId,
            @PathVariable
            @Positive(message = "Booking's id should be positive") Long bookingId
    ) {
        log.info("DELETE Booking request with bookingId {} from bookerId {}", bookingId, bookerId);
        bookingService.deleteBooking(bookerId, bookingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findBookingById(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long userId,
            @PathVariable @Positive(message = "Booking's id should be positive") Long bookingId
    ) {
        log.info("Get Booking request with bookingId {} from userId {}", bookingId, userId);
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return new ResponseEntity<>(
                bookingMapper.toBookingDto(booking),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllByBookerId(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long bookerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GET request to find all bookings by bookerId {} with state {}", bookerId, state);
        List<Booking> bookings = bookingService.getAllByBookerId(bookerId, state, LocalDateTime.now());
        return new ResponseEntity<>(
                bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User's id should be positive") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GET request to find all bookings owned by ownerId {} with state {}", ownerId, state);
        List<Booking> bookings = bookingService.getAllByOwnerId(ownerId, state, LocalDateTime.now());
        return new ResponseEntity<>(
                bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
