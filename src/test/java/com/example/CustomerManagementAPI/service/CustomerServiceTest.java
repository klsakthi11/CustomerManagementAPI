package com.example.CustomerManagementAPI.service;

import com.example.CustomerManagementAPI.model.Customer;
import com.example.CustomerManagementAPI.model.CustomerDTO;
import com.example.CustomerManagementAPI.model.Tier;
import com.example.CustomerManagementAPI.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(UUID.randomUUID());
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");

        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setId(UUID.randomUUID());
        testCustomerDTO.setName("Test Customer");
        testCustomerDTO.setEmail("test@example.com");
    }

    @Test
    void createCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO createdDTO = customerService.createCustomer(testCustomerDTO);

        assertNotNull(createdDTO);
        assertEquals(testCustomer.getName(), createdDTO.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getAllCustomers() {
        // Create test data
        Customer customer1 = new Customer();
        customer1.setName("Customer 1");
        customer1.setEmail("customer1@example.com");

        Customer customer2 = new Customer();
        customer2.setName("Customer 2");
        customer2.setEmail("customer2@example.com");

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> customersDTO = customerService.getAllCustomers();

        assertEquals(2, customersDTO.size());
        assertEquals("Customer 1", customersDTO.get(0).getName());
        assertEquals("Customer 2", customersDTO.get(1).getName());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById() {
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));

        Optional<CustomerDTO> found = customerService.getCustomerById(testCustomer.getId());

        assertTrue(found.isPresent());
        assertEquals(testCustomer.getId(), found.get().getId());
    }

    @Test
    void updateCustomer() {
        Customer updatedDetails = new Customer();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@example.com");

        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO updatedCustomerDTO = customerService.updateCustomer(testCustomer.getId(), updatedDetails);

        assertEquals(updatedDetails.getName(), updatedCustomerDTO.getName());
        assertEquals(updatedDetails.getEmail(), updatedCustomerDTO.getEmail());
    }

    @Test
    void deleteCustomer() {
        doNothing().when(customerRepository).deleteById(testCustomer.getId());

        customerService.deleteCustomer(testCustomer.getId());

        verify(customerRepository, times(1)).deleteById(testCustomer.getId());
    }

    @Test
    void calculateTier_Silver() {
        testCustomer.setAnnualSpend(new BigDecimal("500"));
        customerService.calculateTier(testCustomer);
        assertEquals(Tier.SILVER, testCustomer.getTier());
    }

    @Test
    void calculateTier_Gold() {
        testCustomer.setAnnualSpend(new BigDecimal("5000"));
        testCustomer.setLastPurchaseDate(LocalDateTime.now().minusMonths(6));
        customerService.calculateTier(testCustomer);
        assertEquals(Tier.GOLD, testCustomer.getTier());
    }

    @Test
    void calculateTier_Platinum() {
        testCustomer.setAnnualSpend(new BigDecimal("15000"));
        testCustomer.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
        customerService.calculateTier(testCustomer);
        assertEquals(Tier.PLATINUM, testCustomer.getTier());
    }

    @Test
    void calculateTier_GoldExpired() {
        testCustomer.setAnnualSpend(new BigDecimal("5000"));
        testCustomer.setLastPurchaseDate(LocalDateTime.now().minusMonths(13));
        customerService.calculateTier(testCustomer);
        assertEquals(Tier.SILVER, testCustomer.getTier());
    }

    @Test
    void calculateTier_PlatinumExpired() {
        testCustomer.setAnnualSpend(new BigDecimal("15000"));
        testCustomer.setLastPurchaseDate(LocalDateTime.now().minusMonths(7));
        customerService.calculateTier(testCustomer);
        assertEquals(Tier.SILVER, testCustomer.getTier());
    }
}
