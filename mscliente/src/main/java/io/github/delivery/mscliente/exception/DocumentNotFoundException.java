package io.github.delivery.mscliente.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String documentNumber) {
        super("No customer found with the document:" + documentNumber);
    }
}
