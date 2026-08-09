package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentRequest(
        @NotNull(message = "Tipo de documento é obrigatório")
        DocumentType type,

        @NotBlank(message = "Valor do documento é obrigatório")
        String value
) {}
