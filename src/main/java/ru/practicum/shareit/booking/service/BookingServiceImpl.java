package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("NoAccess. Attempt to book by a non-existing user with id {}.", userId);
            return new NoAccessException("You don't have access to the booking. Please, log in.");
        });
    }

    private void throwBadSearchRequest(Long userId, String state) {
        log.error("UnsupportedStatus. Searching for booking with non-existing parameter {}, " +
                "from user with id {}.", state, userId);
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public BookingDto createBooking(Long bookerId, BookingRequestDto requestDto) {

        if (!requestDto.getStart().isBefore(requestDto.getEnd())) {
            log.error("BadRequest. The start date of the booking is after the end date for booking id {}.", bookerId);
            throw new BadRequestException("The booking start date must be before the end date.");
        }
        User booker = getUser(bookerId);
        Item item = itemRepository.findById(requestDto.getItemId()).orElseThrow(() -> {
            log.error("NotFound. Attempt to book a non-existing item with id {}.", requestDto.getItemId());
            return new NotFoundException(
                    String.format("Item with id %d does not exist.", requestDto.getItemId())
            );
        });
        if (bookerId.equals(item.getOwnerId())) {
            log.error("NotFound. The owner with id {} is trying to book their own item with id {}.",
                    bookerId, requestDto.getItemId());
            throw new NotFoundException("Item cannot be reserved.");
        } else if (item.getAvailable()) {
            Booking booking = bookingRepository.save(mapper.toBooking(requestDto, booker, item));
            return mapper.toDto(booking);
        } else {
            log.error("BadRequest. Attempt to book item (id {}) with unavailable status.", requestDto.getItemId());
            throw new BadRequestException(
                    String.format("Item with id %d is not available.", requestDto.getItemId())
            );
        }
    }

    @Override
    public BookingDto updateBookingStatus(Long requesterId, Long bookingId, Boolean status) {
        log.info("Request to change the status of booking with id {} from user with id {}",
                bookingId, requesterId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("NotFound. Attempt to update status of non-existing booking with id {}.", bookingId);
            return new NotFoundException(
                    String.format("Booking with id %d does not exist.", bookingId)
            );
        });
        if (!booking.getItem().getOwnerId().equals(requesterId)) {
            log.error("NotFound. Attempt to update the status of booking (id {}) not from the owner (id {}) of the item",
                    bookingId, requesterId);
            throw new BadRequestException("You don't have access to update this booking.");
        } else if (status && booking.getStatus().equals(BookingStatus.WAITING)) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);
        } else if (!status && booking.getStatus().equals(BookingStatus.WAITING)) {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);
        } else {
            log.error("BadRequest. Attempt to update the status of booking with id {} again.", bookingId);
            throw new BadRequestException("The status of this booking has already been changed.");
        }
        return mapper.toDto(booking);
    }

    @Override
    public Long deleteBooking(Long ownerId, Long bookingId) {
        log.info("Request to delete booking with id {} from user with id {}", bookingId, ownerId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (booking.getBooker().getId().equals(ownerId)) {
                bookingRepository.deleteById(bookingId);
            } else {
                log.error("NoAccess. Request from user with id {} to delete booking with id {}.", ownerId, bookingId);
                throw new NoAccessException("You don't have access to delete this booking.");
            }
        }
        return bookingId;
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        log.info("Request to view booking with id {} from user with id {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Request to get non-existing booking with id {}.", bookingId);
            return new NotFoundException(
                    String.format("Booking with id %d does not exist.", bookingId)
            );
        });

        /*
        Получение данных о бронировании может быть выполнено
        либо автором бронирования, либо владельцем вещи
        */
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwnerId().equals(userId)) {
            return mapper.toDto(booking);
        } else {
            log.error("Not found. Request from user with id {} to view booking with id {}.", userId, bookingId);
            throw new NotFoundException("This booking isn't found.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllByOwnerId(Long ownerId, String state, LocalDateTime currentTime) {
        userService.checkUserId(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "endDate");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "CURRENT":
                log.info("Request from item owner with id {} to get the list of current bookings.", ownerId);
                bookings = bookingRepository.findCurrentByOwner(ownerId, currentTime, sort);
                break;
            case "FUTURE":
                log.info("Request from item owner with id {} to get the list of future bookings.", ownerId);
                bookings = bookingRepository.findFutureByOwner(ownerId, currentTime, sort);
                break;
            case "WAITING":
                log.info("Request from item owner with id {} to get the list of bookings awaiting confirmation.",
                        ownerId);
                bookings = bookingRepository.findByOwnerByStatus(ownerId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                log.info("Request from item owner with id {} to get the list of rejected bookings.", ownerId);
                bookings = bookingRepository.findByOwnerByStatus(ownerId, BookingStatus.REJECTED, sort);
                break;
            case "PAST":
                log.info("Request from item owner with id {} to get the list of completed bookings.", ownerId);
                bookings = bookingRepository.findPastByOwner(ownerId, currentTime, sort);
                break;
            case "ALL":
                log.info("Request from item owner with id {} to get the list of all bookings.", ownerId);
                bookings = bookingRepository.findAllByOwner(ownerId, sort);
                break;
            default:
                throwBadSearchRequest(ownerId, state);
        }
        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllByBookerId(Long bookerId, String state, LocalDateTime currentTime) {
        userService.checkUserId(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "CURRENT":
                log.info("Request from booker with id {} to get the list of current bookings.", bookerId);
                bookings = bookingRepository.findCurrentByBookerId(bookerId, currentTime, sort);
                break;
            case "FUTURE":
                log.info("Request from booker with id {} to get the list of future bookings.", bookerId);
                bookings = bookingRepository.findByBookerIdAndStartDateIsAfter(bookerId, currentTime, sort);
                break;
            case "WAITING":
                log.info("Request from booker with id {} to get the list of bookings awaiting confirmation.",
                        bookerId);
                bookings = bookingRepository.findByBookerIdAndStatusIs(bookerId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                log.info("Request from booker with id {} to get the list of rejected bookings.", bookerId);
                bookings = bookingRepository.findByBookerIdAndStatusIs(bookerId, BookingStatus.REJECTED, sort);
                break;
            case "PAST":
                log.info("Request from booker with id {} to get the list of completed bookings.", bookerId);
                bookings = bookingRepository.findByBookerIdAndEndDateIsBefore(bookerId, currentTime, sort);
                break;
            case "ALL":
                log.info("Request from booker with id {} to get the list of all bookings.", bookerId);
                bookings = bookingRepository.findByBookerId(bookerId, sort);
                break;
            default:
                throwBadSearchRequest(bookerId, state);
        }
        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
