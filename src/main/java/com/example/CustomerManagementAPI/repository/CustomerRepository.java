package com.example.CustomerManagementAPI.repository;

import com.example.CustomerManagementAPI.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);

    List<Customer> findByNameContainingIgnoreCase(String name);
}
