package com.bank.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "account_no", nullable = false)
    private Long accountNo;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(nullable = false)
    private String status; // APPROVED, REJECTED

    private String remarks;

    @Builder.Default
    @Column(name = "approved_date")
    private LocalDateTime approvedDate = LocalDateTime.now();
}
