package ru.practicum.gateway.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CommentRequestDto {

    @NotBlank
    @Size(max = 1000, message = "Text's size shouldn't be more than 1000 characters")
    private String text;
}