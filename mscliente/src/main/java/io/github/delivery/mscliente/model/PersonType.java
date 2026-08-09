package io.github.delivery.mscliente.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PersonType {

    JURIDICA("Pessoa Jurídica"),
    FISICA("Pessoa Física");

    @JsonValue
    private final String description;
}
