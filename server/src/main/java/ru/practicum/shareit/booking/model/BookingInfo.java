package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NonNull
@Builder
public class BookingInfo {

    Booking lastBooking;

    Booking nextBooking;
}
