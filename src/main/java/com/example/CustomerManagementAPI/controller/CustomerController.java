package com.example.CustomerManagementAPI.controller;

import com.example.CustomerManagementAPI.model.CustomerDTO;
import com.example.CustomerManagementAPI.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        //Aad current time, if user doesn't provide the value
        if(customerDTO.getLastPurchaseDate() == null) {
            customerDTO.setLastPurchaseDate(LocalDateTime.now());
        }
        CustomerDTO createdCustomerDTO = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomerDTO);
    }

    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "List of all customers",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerDTO.class))})
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customersDTO = customerService.getAllCustomers();
        return ResponseEntity.ok(customersDTO);
    }

    @Operation(summary = "Get a customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the customer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "ID of the customer to be retrieved") @PathVariable UUID id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get customers by name")
    @ApiResponse(responseCode = "200", description = "Found customers",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerDTO.class))})
    @GetMapping(params = "name")
    public ResponseEntity<List<CustomerDTO>> getCustomersByName(
            @Parameter(description = "Name to search for") @RequestParam(required = false) String name) {
        if (name != null) {
            return ResponseEntity.ok(customerService.getCustomersByName(name));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get customers by email")
    @ApiResponse(responseCode = "200", description = "Found customers",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerDTO.class))})
    @GetMapping(params = "email")
    public ResponseEntity<List<CustomerDTO>> getCustomersByEmail(
            @Parameter(description = "Email to search for") @RequestParam(required = false) String email) {
        if (email != null) {
            return customerService.getCustomerByEmail(email)
                    .map(customerDTO -> ResponseEntity.ok(List.of(customerDTO)))
                    .orElse(ResponseEntity.ok(List.of()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<Optional<CustomerDTO>> updateCustomer(
            @Parameter(description = "ID of the customer to be updated") @PathVariable UUID id,
            @RequestBody CustomerDTO customerDetails) {
        try {
            Optional<CustomerDTO> updatedCustomer = customerService.updateCustomer(id, customerDetails);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to be deleted") @PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
