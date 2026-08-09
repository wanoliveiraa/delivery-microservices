package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.PersonType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        UUID userId,
        String name,
        String phone,
        PersonType personType,
        List<AddressResponse> addresses,
        List<DocumentResponse> documents,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
