package com.example.CustomerManagementAPI.service;

import com.example.CustomerManagementAPI.model.Customer;
import com.example.CustomerManagementAPI.model.CustomerDTO;
import com.example.CustomerManagementAPI.model.Tier;
import com.example.CustomerManagementAPI.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setAnnualSpend(customerDTO.getAnnualSpend());
        customer.setLastPurchaseDate(customerDTO.getLastPurchaseDate());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());

    }

    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return customerRepository.findById(id).map(this::convertToDTO);
    }

    public List<CustomerDTO> getCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<CustomerDTO> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).map(this::convertToDTO);
    }

    public Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setName(customerDTO.getName());
                    customer.setEmail(customerDTO.getEmail());
                    customer.setAnnualSpend(customerDTO.getAnnualSpend());
                    customer.setLastPurchaseDate(customerDTO.getLastPurchaseDate());
                    Customer updatedCustomer = customerRepository.save(customer);
                    return convertToDTO(updatedCustomer);
                });
    }

    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }


    public Tier calculateTier(Customer customer) {
        if (customer.getAnnualSpend() == null) {
            return Tier.SILVER;
        }

        BigDecimal annualSpend = customer.getAnnualSpend();
        LocalDateTime lastPurchaseDate = customer.getLastPurchaseDate();

        if (annualSpend.compareTo(new BigDecimal("10000")) >= 0) {
            if (lastPurchaseDate != null && lastPurchaseDate.isAfter(LocalDateTime.now().minusMonths(6))) {
                return Tier.PLATINUM;
            } else {
                return Tier.SILVER;
            }
        } else if (annualSpend.compareTo(new BigDecimal("1000")) >= 0) {
            if (lastPurchaseDate != null && lastPurchaseDate.isAfter(LocalDateTime.now().minusMonths(12))) {
                return Tier.GOLD;
            } else {
                return Tier.SILVER;
            }
        } else {
            return Tier.SILVER;
        }
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAnnualSpend(customer.getAnnualSpend());
        dto.setLastPurchaseDate(customer.getLastPurchaseDate());
        dto.setTier(calculateTier(customer));
        return dto;
    }

}
