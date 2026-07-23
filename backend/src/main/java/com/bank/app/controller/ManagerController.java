package com.bank.app.controller;

import com.bank.app.dto.ApiResponse;
import com.bank.app.dto.ApprovalRequest;
import com.bank.app.entity.Account;
import com.bank.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Bank Manager only endpoints - protected via SecurityConfig (ROLE_BANK_MANAGER)
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final AccountService accountService;

    @GetMapping("/accounts/pending")
    public ApiResponse<List<Account>> getPendingAccounts() {
        return ApiResponse.ok("Pending accounts fetched", accountService.getPendingAccounts());
    }

    @PutMapping("/accounts/{accountNo}/approve")
    public ApiResponse<Account> approveAccount(@PathVariable Long accountNo,
                                                @Valid @RequestBody ApprovalRequest request) {
        return ApiResponse.ok("Account approved", accountService.approveAccount(accountNo, request, null));
    }

    @PutMapping("/accounts/{accountNo}/reject")
    public ApiResponse<Account> rejectAccount(@PathVariable Long accountNo,
                                               @Valid @RequestBody ApprovalRequest request) {
        return ApiResponse.ok("Account rejected", accountService.rejectAccount(accountNo, request, null));
    }
}
