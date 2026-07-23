package com.bank.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransactionRequest {
    @NotNull
    private Long accountNo;
    @NotNull
    @Positive
    private BigDecimal amount;
}
