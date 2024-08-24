package ru.practicum.gateway.item;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.comment.dto.CommentRequestDto;
import ru.practicum.gateway.item.dto.CreateItemDto;
import ru.practicum.gateway.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> patchItem(Long ownerId, Long itemId, Map<String, Object> fields) {
        return patch("/" + itemId, ownerId, fields);
    }

    public ResponseEntity<Object> getItemById(Long ownerId, Long itemId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> search (String text) {
        return get(String.format("/search?text=%s", text));
    }

    public ResponseEntity<Object> createComment(Long itemId, Long authorId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", authorId, commentRequestDto);
    }

}
