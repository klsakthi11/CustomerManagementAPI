package com.example.CustomerManagementAPI.controller;

import com.example.CustomerManagementAPI.model.CustomerDTO;
import com.example.CustomerManagementAPI.model.Tier;
import com.example.CustomerManagementAPI.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        UUID customerId = UUID.randomUUID();
        customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);
        customerDTO.setName("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setAnnualSpend(new BigDecimal("5000"));
        customerDTO.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
        customerDTO.setTier(Tier.GOLD);
    }

    @Test
    void createCustomer() {

        //when(customerService.createCustomer(any(customerDTO.class))).thenReturn(customerDTO);
        when(customerService.createCustomer(customerDTO)).thenReturn(customerDTO);
        ResponseEntity<CustomerDTO> response = customerController.createCustomer(customerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test", response.getBody().getName());
    }

    @Test
    void getAllCustomers() {
        // Create test data
        CustomerDTO customerDTO1 = new CustomerDTO();
        customerDTO1.setName("Customer 1");

        CustomerDTO customerDTO2 = new CustomerDTO();
        customerDTO2.setName("Customer 2");

        when(customerService.getAllCustomers()).thenReturn(List.of(customerDTO1, customerDTO2));

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Customer 1", response.getBody().get(0).getName());
        assertEquals("Customer 2", response.getBody().get(1).getName());

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById() {
        UUID customerId= UUID.randomUUID();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);
        customerDTO.setName("Test");

        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customerDTO));

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(customerId, response.getBody().getId());
    }

    @Test
    void getCustomerById_NotFound() {
        UUID id = UUID.randomUUID();

        when(customerService.getCustomerById(id)).thenReturn(Optional.empty());

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCustomersByName() {
        CustomerDTO customerDTO1 = new CustomerDTO();
        customerDTO1.setName("John");
        CustomerDTO customerDTO2 = new CustomerDTO();
        customerDTO1.setName("Johnny");

        when(customerService.getCustomersByName("john")).thenReturn(Arrays.asList(customerDTO1, customerDTO2));

        ResponseEntity<List<CustomerDTO>> response = customerController.getCustomersByName("john");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getCustomerByEmail() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setEmail("test@example.com");

        when(customerService.getCustomerByEmail("test@example.com")).thenReturn(Optional.of(customerDTO));

        ResponseEntity<List<CustomerDTO>> response = customerController.getCustomersByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("test@example.com", response.getBody().getFirst().getEmail());
    }

    @Test
    void updateCustomer() {
        UUID id = UUID.randomUUID();
        CustomerDTO existing = new CustomerDTO();
        existing.setId(id);
        existing.setName("Old Name");

        CustomerDTO updates = new CustomerDTO();
        updates.setName("New Name");

        when(customerService.updateCustomer(id, updates)).thenReturn(Optional.of(existing));

        ResponseEntity<Optional<CustomerDTO>> response = customerController.updateCustomer(id, updates);
                //customerController.updateCustomer(id, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        //assertEquals(id, response.getBody().getId());
    }

    @Test
    void deleteCustomer() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = customerController.deleteCustomer(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
