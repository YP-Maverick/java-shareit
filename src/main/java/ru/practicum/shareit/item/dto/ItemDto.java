package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@With
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor(force = true)
public class ItemDto {

    Long id;

    @NotBlank(message = "Name shouldn't be empty.")
    @Size(max = 60, message = "Name's size shouldn't be more than 60 characters")
    String name;

    @NotBlank(message = "Description shouldn't be empty.")
    @Size(max = 200, message = "Description's size shouldn't be more than 200 characters")
    String description;

    @NotNull(message = "Status 'available' shouldn't be empty.")
    Boolean available;

    BookingItemDto lastBooking;

    BookingItemDto nextBooking;

    List<CommentDto> comments;
}
