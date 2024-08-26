package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CommentRequestDto {

    @NotBlank
    @Size(max = 1000, message = "Text's size shouldn't be more than 1000 characters")
    private String text;
}