package com.bank.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApprovalRequest {
    @NotBlank
    private String remarks;
}
