package io.github.delivery.mscliente.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "A number is required")
        String number,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Status is required")
        String state,

        String complement,

        @NotBlank(message = "ZIP code is required")
        String zipCode
) {}
