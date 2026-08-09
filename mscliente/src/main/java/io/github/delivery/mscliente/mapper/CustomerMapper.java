package io.github.delivery.mscliente.mapper;

import io.github.delivery.mscliente.dto.*;
import io.github.delivery.mscliente.model.Address;
import io.github.delivery.mscliente.model.Customer;
import io.github.delivery.mscliente.model.Document;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Customer toCustomer(CustomerRequest customerRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "documents", ignore = true)
    void updateEntityFromRequest(CustomerRequest request, @MappingTarget Customer customer);

    Address toAddressEntity(AddressRequest request);

    Document toDocumentEntity(DocumentRequest request);

    CustomerResponse toResponse(Customer customer);


}
