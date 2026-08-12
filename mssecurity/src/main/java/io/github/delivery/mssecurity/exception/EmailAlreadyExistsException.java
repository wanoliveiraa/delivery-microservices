package io.github.delivery.mssecurity.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("There is already a user registered with this email addres " + email);
    }
}
