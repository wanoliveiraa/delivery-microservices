package io.github.delivery.mscliente.service;

import io.github.delivery.mscliente.dto.CustomerRequest;
import io.github.delivery.mscliente.dto.CustomerResponse;
import io.github.delivery.mscliente.exception.CustomerAlreadyExistsException;
import io.github.delivery.mscliente.exception.CustomerNotFoundException;
import io.github.delivery.mscliente.exception.DocumentNotFoundException;
import io.github.delivery.mscliente.mapper.CustomerMapper;
import io.github.delivery.mscliente.model.Address;
import io.github.delivery.mscliente.model.Customer;
import io.github.delivery.mscliente.model.Document;
import io.github.delivery.mscliente.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerRequest customerRequest) {

        if (customerRepository.existsByUserId(customerRequest.userId())) {
            throw new CustomerAlreadyExistsException(customerRequest.userId());
        }
        var customer = customerMapper.toCustomer(customerRequest);

        syncAddresses(customer, customerRequest);
        syncDocuments(customer, customerRequest);

        var savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);

    }

    public CustomerResponse getCustomerByUserId(UUID userId) {
        var customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException(userId));

        return customerMapper.toResponse(customer);
    }

    public Page<CustomerResponse> findAllCustomer(Pageable pageable){

        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);

    }

    public CustomerResponse getCustomerDetailsById(UUID id) {
        var customer = customerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    public CustomerResponse findByDocument(String documentNumber) {
        var customer = customerRepository.findByDocumentsValue(documentNumber)
                .orElseThrow(() -> new DocumentNotFoundException(documentNumber));

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id,CustomerRequest customerRequest) {

        var customer = customerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));


        customerMapper.updateEntityFromRequest(customerRequest, customer);

        syncAddresses(customer, customerRequest);
        syncDocuments(customer, customerRequest);

        var updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);

    }

    private void syncAddresses(Customer customer, CustomerRequest customerRequest) {

        if (customerRequest.addresses() != null) {
            customer.getAddresses().clear();
            customerRequest.addresses().forEach(dto -> {
                var address = customerMapper.toAddressEntity(dto);
                address.setCustomer(customer);
                customer.getAddresses().add(address);
            });
        }
    }
    private void syncDocuments(Customer customer, CustomerRequest customerRequest) {

        if (customerRequest.documents() != null) {
            customer.getDocuments().clear();
            customerRequest.documents().forEach(dto -> {
                var document = customerMapper.toDocumentEntity(dto);
                document.setCustomer(customer);
                customer.getDocuments().add(document);
            });
        }
    }
    @Transactional
    public void deleteCustomer(UUID id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerRepository.delete(customer);
    }
    @Transactional
    public void deleteCustomerSoft(UUID id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }



}
