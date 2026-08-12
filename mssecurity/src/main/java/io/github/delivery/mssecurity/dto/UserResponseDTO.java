package io.github.delivery.mssecurity.dto;

import java.util.UUID;

public record UserResponseDTO (

        UUID id,
        String email
){
}
