package com.example.CustomerManagementAPI.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerDTO {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private BigDecimal annualSpend;

    private LocalDateTime lastPurchaseDate;

    @Transient
    private Tier tier;

}
