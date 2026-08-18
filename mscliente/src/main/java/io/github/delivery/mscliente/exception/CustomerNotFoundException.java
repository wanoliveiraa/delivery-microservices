package io.github.delivery.mscliente.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID id) {
        super("Client not found with id: " + id);
    }
}
