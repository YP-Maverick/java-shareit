package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingRequest creationDto;
    private MockHttpServletResponse response;
    private MvcResult result;

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingRequest getForcreateBookingDto() {
        return BookingRequest.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(1L)
                .build();
    }

    private MockHttpServletResponse createBookingResponse(Long bookerId,
                                                          BookingRequest dto) throws Exception {
        result = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @DisplayName("Добавить бронирование")
    @Test
    void shouldCreateBooking() throws Exception {
        creationDto = getForcreateBookingDto();
        booking = createBooking();
        bookingDto = createBookingDto();
        when(bookingService.createBooking(anyLong(), any(BookingRequest.class)))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class)))
                .thenReturn(bookingDto);

        response = createBookingResponse(1L, creationDto);

        assertEquals(201, response.getStatus());
        assertEquals(mapper.writeValueAsString(bookingDto), response.getContentAsString());

        verify(bookingService, times(1))
                .createBooking(anyLong(), any(BookingRequest.class));
        verifyNoMoreInteractions(bookingService);
    }

    private MockHttpServletResponse updateStatusResponse(Long ownerId,
                                                         Long bookingId,
                                                         Boolean approved) throws Exception {
        result = mvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @DisplayName("Обновить статуса бронирования")
    @Test
    void shouldUpdateStatus() throws Exception {
        booking = createBooking();
        bookingDto = createBookingDto();
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class)))
                .thenReturn(bookingDto);

        response = updateStatusResponse(1L, 1L, true);

        assertEquals(200, response.getStatus());
        assertEquals(mapper.writeValueAsString(bookingDto), response.getContentAsString());

        verify(bookingService, times(1))
                .updateBookingStatus(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingService);
    }

    private MockHttpServletResponse deleteBookingResponse(Long bookerId, Long bookingId) throws Exception {
        result = mvc.perform(delete("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", bookerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @DisplayName("Удалить бронирование")
    @Test
    void shouldDelete() throws Exception {
        when(bookingService.deleteBooking(anyLong(), anyLong()))
                .thenReturn(1L);

        response = deleteBookingResponse(1L, 1L);
        assertEquals(204, response.getStatus());

        verify(bookingService, times(1))
                .deleteBooking(anyLong(), anyLong());
        verifyNoMoreInteractions(bookingService);
    }

    private MockHttpServletResponse findByIdBookingResponse(Long userId, Long bookingId) throws Exception {
        result = mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @DisplayName("Получить бронирование по id")
    @Test
    void shouldFindById() throws Exception {
        booking = createBooking();
        bookingDto = createBookingDto();
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class)))
                .thenReturn(bookingDto);

        response = findByIdBookingResponse(1L, 1L);
        assertEquals(200, response.getStatus());
        assertEquals(mapper.writeValueAsString(bookingDto), response.getContentAsString());

        verify(bookingService, times(1))
                .getBookingById(anyLong(), anyLong());
        verifyNoMoreInteractions(bookingService);
    }
}