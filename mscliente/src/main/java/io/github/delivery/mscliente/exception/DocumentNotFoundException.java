package io.github.delivery.mscliente.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String documentNumber) {
        super("Nenhum cliente encontrado com o documento:" + documentNumber);
    }
}
