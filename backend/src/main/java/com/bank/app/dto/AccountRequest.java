package com.bank.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountRequest {
    @NotNull
    private Long customerId;
    @NotBlank
    private String accountType; // SAVINGS, CURRENT
}
