package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@SpringBootTest
public class ShareItServerTest {

    @MockBean
    BookingMapper bookingMapper;

    @MockBean
    ItemMapper itemMapper;

    @MockBean
    CommentMapper commentMapper;

    @MockBean
    ItemRequestMapper itemRequestMapper;

    @MockBean
    UserMapper userMapper;

    @Test
    void loadContext() {
    }

}