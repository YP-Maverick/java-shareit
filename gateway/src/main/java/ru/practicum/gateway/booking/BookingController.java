package ru.practicum.gateway.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.gateway.booking.dto.BookingState;

import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.CreateBookingDto;
import ru.practicum.gateway.exception.BadRequestException;
import ru.practicum.gateway.exception.UnsupportedStatusException;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long bookerId,
			@RequestBody @Valid CreateBookingDto creationDto) {

		if (!creationDto.getStart().isBefore(creationDto.getEnd())) {
			log.error("BadRequest. Start date is after end date for booking by userId {}.", bookerId);
			throw new BadRequestException("The booking start date must be before the end date.");
		}

		log.info("Creating booking for itemId {} by userId {}", creationDto.getItemId(), bookerId);
		return bookingClient.createBooking(bookerId, creationDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookingStatus(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long ownerId,
			@PathVariable
			@Positive(message = "Booking's id should be positive") Long bookingId,
			@RequestParam Boolean approved) {

		log.info("Updating status for bookingId {} by userId {}", bookingId, ownerId);
		return bookingClient.updateStatus(ownerId, bookingId, approved);
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<Object> deleteBooking(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long bookerId,
			@PathVariable
			@Positive(message = "Booking's id should be positive") Long bookingId) {

		log.info("Deleting bookingId {} by userId {}", bookingId, bookerId);
		return bookingClient.deleteBooking(bookerId, bookingId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long userId,
			@PathVariable
			@Positive(message = "Booking's id should be positive") Long bookingId) {

		log.info("Getting bookingId {} by userId {}", bookingId, userId);
		return bookingClient.findBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsForBooker(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long bookerId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(defaultValue = "0")
			@PositiveOrZero(message = "Parameter 'from' should be positive or zero") int from,
			@RequestParam(defaultValue = "10")
			@Positive(message = "Parameter 'size' should be positive") int size) {

		BookingState state = BookingState.from(stateParam).orElseThrow(
				() -> throwUnsupportedStatus(bookerId, stateParam)
		);

		log.info("Getting all bookings for userId {}", bookerId);
		return bookingClient.findAllBookingsByBooker(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForOwner(
			@RequestHeader("X-Sharer-User-Id")
			@Positive(message = "User's id should be positive") Long ownerId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(defaultValue = "0")
			@PositiveOrZero(message = "Parameter 'from' should be positive or zero") int from,
			@RequestParam(defaultValue = "10")
			@Positive(message = "Parameter 'size' should be positive") int size) {

		BookingState state = BookingState.from(stateParam).orElseThrow(
				() -> throwUnsupportedStatus(ownerId, stateParam)
		);

		log.info("Getting all bookings for ownerId {}", ownerId);
		return bookingClient.findAllBookingsByOwner(ownerId, state, from, size);
	}

	private UnsupportedStatusException throwUnsupportedStatus(Long userId, String state) {
		log.error("UnsupportedStatus. Searching bookings with unsupported parameter {}, from userId {}.", state, userId);
		throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
	}
}

