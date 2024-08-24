package ru.practicum.gateway.request.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@With
@Value
@Builder
@Jacksonized
public class CreateItemRequestDto {

    String description;
}