package com.bank.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long txnId;

    @Column(name = "account_no", nullable = false)
    private Long accountNo;

    @Column(name = "txn_type", nullable = false)
    private String txnType; // DEPOSIT, WITHDRAW

    @Column(nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "txn_date")
    private LocalDateTime txnDate = LocalDateTime.now();

    @Column(name = "cashier_id")
    private Long cashierId;
}
