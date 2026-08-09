package io.github.delivery.mscliente.dto;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String number,
        String city,
        String state,
        String complement,
        String zipCode)
{}
