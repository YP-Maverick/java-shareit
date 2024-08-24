package ru.practicum.gateway.booking.dto;



import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@With
@Value
@Builder
public class CreateBookingDto {
    @NotNull(message = "Start time shouldn't be null.")
    @Future(message = "Start time should be in future.")
    LocalDateTime start;
    @NotNull(message = "End time shouldn't be null.")
    @Future(message = "End time should be in future.")
    LocalDateTime end;
    @NotNull(message = "Item's id shouldn't be null.")
    @Positive(message = "End time should be positive.")
    Long itemId;
}