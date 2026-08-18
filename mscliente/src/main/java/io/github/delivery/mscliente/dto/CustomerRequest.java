package io.github.delivery.mscliente.dto;

import io.github.delivery.mscliente.model.PersonType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        String phone,

        @NotNull(message = "Person type is required")
        PersonType personType,

        List< @Valid DocumentRequest> documents,


        List< @Valid AddressRequest> addresses
) {}
