package io.github.delivery.mscliente.service;

import io.github.delivery.mscliente.dto.AddressRequest;
import io.github.delivery.mscliente.dto.CustomerRequest;
import io.github.delivery.mscliente.dto.CustomerResponse;
import io.github.delivery.mscliente.dto.DocumentRequest;
import io.github.delivery.mscliente.exception.CustomerAlreadyExistsException;
import io.github.delivery.mscliente.exception.CustomerNotFoundException;
import io.github.delivery.mscliente.exception.DocumentNotFoundException;
import io.github.delivery.mscliente.mapper.CustomerMapper;
import io.github.delivery.mscliente.model.*;
import io.github.delivery.mscliente.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String userId, String role) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn(userId);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        doReturn(authorities).when(authentication).getAuthorities();
    }

    private CustomerRequest createDummyRequest() {
        return new CustomerRequest(
                "Cliente Teste",
                "98999999999",
                PersonType.FISICA,
                List.of(new DocumentRequest(DocumentType.CPF, "12345678900")),
                List.of(new AddressRequest("Rua A", "100", "São Luís", "MA", null, "65000000"))
        );
    }

    private Customer createDummyCustomer(UUID id, UUID userId) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUserId(userId);
        customer.setName("Cliente Teste");
        customer.setPhone("98999999999");
        customer.setPersonType(PersonType.FISICA);

        customer.setAddresses(new HashSet<>());
        customer.setDocuments(new HashSet<>());

        return customer;
    }

    @Test
    void createCustomerSuccessfully() {
        UUID userId = UUID.randomUUID();
        CustomerRequest request = createDummyRequest();
        Customer customer = createDummyCustomer(UUID.randomUUID(), userId);
        CustomerResponse response = mock(CustomerResponse.class);

        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(customerMapper.toCustomer(request)).thenReturn(customer);
        when(customerMapper.toAddressEntity(any())).thenReturn(new Address());
        when(customerMapper.toDocumentEntity(any())).thenReturn(new Document());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.createCustomer(request, userId);

        assertNotNull(result);
        verify(customerRepository).existsByUserId(userId);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerExists() {
        UUID userId = UUID.randomUUID();
        CustomerRequest request = createDummyRequest();

        when(customerRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class,
                () -> customerService.createCustomer(request, userId));

        verify(customerRepository, never()).save(any());
    }


    @Test
    void getCustomerByUserIdSuccess() {
        UUID userId = UUID.randomUUID();
        Customer customer = createDummyCustomer(UUID.randomUUID(), userId);
        CustomerResponse response = mock(CustomerResponse.class);

        mockSecurityContext(userId.toString(), "ROLE_CLIENT");

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.getCustomerByUserId(userId);

        assertNotNull(result);
        verify(customerRepository).findByUserId(userId);
    }

    @Test
    void getCustomerByUserIdNotFound() {
        UUID userId = UUID.randomUUID();

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerByUserId(userId));
    }

    @Test
    void getCustomerByUserIdAccessDenied() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Customer customer = createDummyCustomer(UUID.randomUUID(), ownerUserId);

        mockSecurityContext(otherUserId.toString(), "ROLE_USER");

        when(customerRepository.findByUserId(ownerUserId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.getCustomerByUserId(ownerUserId));
    }


    @Test
    void findAllCustomerSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer customer = createDummyCustomer(UUID.randomUUID(), UUID.randomUUID());
        Page<Customer> page = new PageImpl<>(List.of(customer));
        CustomerResponse response = mock(CustomerResponse.class);

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        Page<CustomerResponse> result = customerService.findAllCustomer(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).findAll(pageable);
    }


    @Test
    void getCustomerDetailsByIdSuccess() {
        UUID customerId = UUID.randomUUID();
        UUID ownerUserId = UUID.randomUUID();
        Customer customer = createDummyCustomer(customerId, ownerUserId);
        CustomerResponse response = mock(CustomerResponse.class);

        mockSecurityContext("admin-id", "ROLE_ADMIN");

        when(customerRepository.findByIdWithDetails(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.getCustomerDetailsById(customerId);

        assertNotNull(result);
        verify(customerRepository).findByIdWithDetails(customerId);
    }

    @Test
    void getCustomerDetailsByIdNotFound() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findByIdWithDetails(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerDetailsById(customerId));
    }


    @Test
    void findByDocumentSuccess() {
        String docNumber = "12345678900";
        Customer customer = createDummyCustomer(UUID.randomUUID(), UUID.randomUUID());
        CustomerResponse response = mock(CustomerResponse.class);

        when(customerRepository.findByDocumentsValue(docNumber)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.findByDocument(docNumber);

        assertNotNull(result);
        verify(customerRepository).findByDocumentsValue(docNumber);
    }

    @Test
    void findByDocumentNotFound() {
        String docNumber = "00000000000";

        when(customerRepository.findByDocumentsValue(docNumber)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class,
                () -> customerService.findByDocument(docNumber));
    }


    @Test
    void updateCustomerSuccess() {
        UUID customerId = UUID.randomUUID();
        UUID ownerUserId = UUID.randomUUID();
        CustomerRequest request = createDummyRequest();
        Customer customer = createDummyCustomer(customerId, ownerUserId);
        CustomerResponse response = mock(CustomerResponse.class);

        mockSecurityContext(ownerUserId.toString(), "ROLE_USER");

        when(customerRepository.findByIdWithDetails(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toAddressEntity(any())).thenReturn(new Address());
        when(customerMapper.toDocumentEntity(any())).thenReturn(new Document());
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.updateCustomer(customerId, request);

        assertNotNull(result);
        verify(customerMapper).updateEntityFromRequest(request, customer);
        verify(customerRepository).save(customer);
    }


    @Test
    void deleteCustomerSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = createDummyCustomer(customerId, UUID.randomUUID());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(customerId);

        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomerSoftSuccess() {
        UUID customerId = UUID.randomUUID();
        UUID ownerUserId = UUID.randomUUID();
        Customer customer = createDummyCustomer(customerId, ownerUserId);

        mockSecurityContext(ownerUserId.toString(), "ROLE_CLIENT");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.deleteCustomerSoft(customerId);

        assertNotNull(customer.getDeletedAt());
        verify(customerRepository).save(customer);
    }

}