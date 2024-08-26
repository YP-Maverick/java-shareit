package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mappings({
            @Mapping(target = "id", source = "booking.id"),
            @Mapping(target = "start", source = "booking.startDate"),
            @Mapping(target = "end", source = "booking.endDate"),
            @Mapping(target = "status", source = "booking.status"),
            @Mapping(target = "booker", source = "booking.booker"),
            @Mapping(target = "item", source = "booking.item")
    })
    BookingDto toBookingDto(Booking booking);

    @Mappings({
            @Mapping(target = "id", source = "booking.id"),
            @Mapping(target = "start", source = "booking.startDate"),
            @Mapping(target = "end", source = "booking.endDate"),
            @Mapping(target = "status", source = "booking.status"),
            @Mapping(target = "bookerId", source = "booking.booker.id")
    })
    BookingItemDto toItemDto(Booking booking);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "startDate", source = "dto.start"),
            @Mapping(target = "endDate", source = "dto.end"),
            @Mapping(target = "item", source = "item"),
            @Mapping(target = "booker", source = "booker"),
            @Mapping(target = "status", constant = "WAITING")
    })
    Booking toBooking(BookingRequest dto, User booker, Item item);
}

