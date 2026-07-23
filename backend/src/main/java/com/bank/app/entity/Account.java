package com.bank.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_no")
    private Long accountNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "account_type", nullable = false)
    private String accountType; // SAVINGS, CURRENT

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    private String status = "PENDING"; // PENDING, ACTIVE, REJECTED

    @Column(name = "created_by")
    private Long createdBy;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
