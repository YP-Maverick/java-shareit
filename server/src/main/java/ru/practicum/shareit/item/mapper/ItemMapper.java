package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
@Component
public interface ItemMapper {

    ItemDto toDto(Item item);

    @Mappings({
            @Mapping(target = "lastBooking", source = "bookingInfo.lastBooking"),
            @Mapping(target = "nextBooking", source = "bookingInfo.nextBooking"),
    })
    ItemDto toDto(Item item, BookingInfo bookingInfo);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "dto.name"),
            @Mapping(target = "description", source = "dto.description"),
            @Mapping(target = "available", source = "dto.available"),
            @Mapping(target = "ownerId", source = "ownerId"),
            @Mapping(target = "requestId", source = "dto.requestId")
    })
    Item toItem(Long ownerId, ItemDto dto);
}


