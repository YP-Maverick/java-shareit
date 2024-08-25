package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestMapper mapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private ItemRequest itemRequest;
    private CreateItemRequestDto creationDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Request")
                .ownerId(user.getId())
                .creationDate(LocalDateTime.now())
                .build();

        creationDto = CreateItemRequestDto.builder().description("аа").build();
    }

    @DisplayName("Создание нового запроса на предмет")
    @Test
    public void shouldCreateItemRequest() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(mapper.toItemRequest(anyLong(), any(LocalDateTime.class), any(CreateItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest createdRequest = itemRequestService.create(user.getId(), LocalDateTime.now(), creationDto);
        assertNotNull(createdRequest);
        assertEquals(itemRequest.getId(), createdRequest.getId());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @DisplayName("Ошибка при создании запроса, если пользователь не существует")
    @Test
    public void shouldThrowNotFoundExceptionWhenUserNotFoundOnCreate() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.create(user.getId(), LocalDateTime.now(), creationDto);
        });

        assertEquals("User with id 1 does not exist.", exception.getMessage());
        verify(itemRequestRepository, times(0)).save(any(ItemRequest.class));
    }

    @DisplayName("Получение запроса по ID")
    @Test
    public void shouldGetItemRequestById() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest foundRequest = itemRequestService.getById(user.getId(), itemRequest.getId());
        assertNotNull(foundRequest);
        assertEquals(itemRequest.getId(), foundRequest.getId());

        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
    }


    @DisplayName("Ошибка при получении запросов, если пользователь не существует")
    @Test
    public void shouldThrowNotFoundExceptionWhenUserNotFoundOnGetAll() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getByOwnerId(user.getId());
        });

        assertEquals("User with id 1 does not exist.", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @DisplayName("Получение всех запросов")
    @Test
    public void shouldGetAllItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRequestRepository.findByOwnerIdNot(eq(user.getId()), any(Pageable.class))).thenReturn(requests);

        List<ItemRequest> foundRequests = itemRequestService.getAll(user.getId(), 0, 10);
        assertNotNull(foundRequests);
        assertEquals(1, foundRequests.size());
        assertEquals(itemRequest.getId(), foundRequests.get(0).getId());

        verify(userRepository, times(1)).existsById(user.getId());
    }
}
