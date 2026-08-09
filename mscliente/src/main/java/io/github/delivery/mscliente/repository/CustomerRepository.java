package io.github.delivery.mscliente.repository;

import io.github.delivery.mscliente.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Optional<Customer> findByDocumentsValue(String value);

    @Query("""
        SELECT DISTINCT c
        FROM Customer c
        LEFT JOIN FETCH c.documents
        LEFT JOIN FETCH c.addresses
        WHERE c.id = :id
        """)
    Optional<Customer> findByIdWithDetails(@Param("id") UUID id);

}
