package ru.practicum.gateway.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.gateway.booking.dto.BookingItemDto;
import ru.practicum.gateway.item.comment.dto.CommentDto;

import java.util.List;

@With
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor(force = true)
public class ItemDto {

    private Long id;

    @NotBlank(message = "Name shouldn't be empty.")
    @Size(max = 60, message = "Name's size shouldn't be more than 60 characters")
    private String name;

    @NotBlank(message = "Description shouldn't be empty.")
    @Size(max = 200, message = "Description's size shouldn't be more than 200 characters")
    private String description;

    @NotNull(message = "Status 'available' shouldn't be empty.")
    private Boolean available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
