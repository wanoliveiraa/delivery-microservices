package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.PersonType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CustomerRequest(
        @NotNull(message = "ID do usuário é obrigatório")
        UUID userId,

        @NotBlank(message = "Nome é obrigatório")
        String name,

        String phone,

        @NotNull(message = "Tipo de pessoa é obrigatório")
        PersonType personType,

        List< @Valid DocumentRequest> documents,


        List< @Valid AddressRequest> addresses
) {}
