package io.github.delivery.mscliente.controller;

import io.github.delivery.mscliente.dto.CustomerRequest;
import io.github.delivery.mscliente.dto.CustomerResponse;
import io.github.delivery.mscliente.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Cria um novo cliente",
            description = "Cadastra um cliente com seus endereços e documentos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "userId já cadastrado")
    })
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        var created = customerService.createCustomer(request);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);

    }

    @Operation(summary = "Atualiza um cliente existente",
            description = "Atualiza um cliente com seus endereços e documentos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable("id") UUID id, @Valid @RequestBody CustomerRequest request) {
        var updated = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Lista clientes de forma paginada")
    @GetMapping
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerService.findAllCustomer(pageable);
    }

    @Operation(summary = "Busca um cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerDetailsById(@PathVariable("id") UUID id) {
        var customerDetails = customerService.getCustomerDetailsById(id);
        return ResponseEntity.ok(customerDetails);
    }

    @Operation(summary = "Busca um cliente por userID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(@PathVariable("userId") UUID userId) {
        var customer = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Busca um cliente por documento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<CustomerResponse> getCustomerByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        var customer = customerService.findByDocument(documentNumber);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Remove um cliente permanentemente (hard delete)",
            description = "Apaga o cliente e seus dados definitivamente do banco")
    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desativa um cliente (soft delete)",
            description = "Marca o cliente como inativo, preservando o registro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerSoft(@PathVariable("id") UUID id) {
        customerService.deleteCustomerSoft(id);
        return ResponseEntity.noContent().build();
    }

}