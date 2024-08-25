package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private ItemMapper itemMapper;

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .requestId(null)
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .ownerId(1L)
                .requestId(null)
                .build();
    }

    private MockHttpServletResponse createItemResponse(Long userId, ItemDto itemDto) throws Exception {
        return mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Добавить предмет")
    @Test
    public void shouldCreateItem() throws Exception {
        ItemDto itemDto = createItemDto();
        Item createdItem = createItem();

        when(itemMapper.toItem(any(Long.class), any(ItemDto.class))).thenReturn(createdItem);
        when(itemService.createItem(any(Long.class), any(Item.class))).thenReturn(createdItem);
        when(itemMapper.toDto(createdItem)).thenReturn(itemDto);

        MockHttpServletResponse response = createItemResponse(1L, itemDto);

        assertEquals(201, response.getStatus());
        assertEquals(mapper.writeValueAsString(itemDto), response.getContentAsString());
        verify(itemService, times(1)).createItem(any(Long.class), any(Item.class));
        verifyNoMoreInteractions(itemService);
    }

    private MockHttpServletResponse updateItemResponse(Long userId, Long itemId, Map<String, Object> updatedFields) throws Exception {
        return mvc.perform(patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updatedFields))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Получить предмет по id")
    @Test
    public void shouldGetItemById() throws Exception {
        ItemDto itemDto = createItemDto();
        Item item = createItem();

        when(itemService.getItemById(any(Long.class), any(Long.class), any())).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        MockHttpServletResponse response = mvc.perform(get("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(itemService, times(1)).getItemById(any(Long.class), any(Long.class), any());
    }

    @DisplayName("Получить все предметы")
    @Test
    public void shouldGetAllItems() throws Exception {
        ItemDto itemDto = createItemDto();
        when(itemService.getAllItemsByUserId(any(Long.class), any())).thenReturn(List.of(createItem()));
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService, times(1)).getAllItemsByUserId(any(Long.class), any());
        verifyNoMoreInteractions(itemService);
    }

    private MockHttpServletResponse deleteItemResponse(Long userId, Long itemId) throws Exception {
        return mvc.perform(delete("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @DisplayName("Создать комментарий к предмету")
    @Test
    public void shouldCreateComment() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("This is a comment");

        // Mocked comment creation response
        String expectedCommentDto = "{\"id\":1,\"authorName\":\"User\",\"text\":\"This is a comment\"}";

        when(itemService.createComment(any(Long.class), any(Long.class), any(LocalDateTime.class), any(CommentRequestDto.class)))
                .thenReturn(null); // Adjust this if you expect a specific return

        MockHttpServletResponse response = mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(201, response.getStatus());
        verify(itemService, times(1)).createComment(any(Long.class), any(Long.class), any(LocalDateTime.class), any(CommentRequestDto.class));
        verifyNoMoreInteractions(itemService);
    }
}
