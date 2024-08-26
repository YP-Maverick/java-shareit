package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookingRequest {

    @NotNull(message = "Start time shouldn't be null.")
    @Future(message = "Start time should be in future.")
    private LocalDateTime start;

    @NotNull(message = "End time shouldn't be null.")
    @Future(message = "End time should be in future.")
    private LocalDateTime end;

    @NotNull(message = "Item's id shouldn't be null.")
    @Positive(message = "End time should be positive.")
    private Long itemId;
}