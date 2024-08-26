package ru.practicum.gateway.booking.dto;


import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingItemDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    Long bookerId;
}