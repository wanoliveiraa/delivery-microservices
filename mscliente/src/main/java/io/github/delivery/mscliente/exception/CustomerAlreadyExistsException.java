package io.github.delivery.mscliente.exception;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(UUID userId) {
        super("Já existe um cliente cadastrado para o userId: " + userId);
    }
}
