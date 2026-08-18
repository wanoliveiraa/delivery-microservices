package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentRequest(
        @NotNull(message = "Document type is required")
        DocumentType type,

        @NotBlank(message = "The document value is required")
        String value
) {}
