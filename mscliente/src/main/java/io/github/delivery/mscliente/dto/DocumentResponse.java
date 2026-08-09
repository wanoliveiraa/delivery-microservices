package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.DocumentType;

import java.util.UUID;

public record DocumentResponse(
        UUID id,
        DocumentType type,
        String value
) {}
