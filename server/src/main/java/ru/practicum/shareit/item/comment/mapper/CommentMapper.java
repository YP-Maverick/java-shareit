package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.time.LocalDateTime;


import org.mapstruct.Mappings; // Импортируйте Mappings
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;


@Mapper(componentModel = "spring", uses = {UserService.class, ItemService.class})
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(comment.getAuthor() != null ? comment.getAuthor().getName() : null)")
    CommentDto toCommentDto(Comment comment);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "itemId", source = "itemId"),
            @Mapping(target = "author.id", source = "authorId"),
            @Mapping(target = "created", source = "createdTime"),
            @Mapping(target = "text", source = "commentRequestDto.text")
    })
    Comment toComment(Long itemId, Long authorId, LocalDateTime createdTime, CommentRequestDto commentRequestDto);
}
