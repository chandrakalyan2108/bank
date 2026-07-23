package com.bank.app.controller;

import com.bank.app.dto.AccountRequest;
import com.bank.app.dto.ApiResponse;
import com.bank.app.entity.Account;
import com.bank.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Cashier creates account -> status PENDING
    @PostMapping
    public ApiResponse<Account> createAccount(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.createAccount(request, null);
        return ApiResponse.ok("Account created with PENDING status", account);
    }

    @GetMapping
    public ApiResponse<List<Account>> getAllAccounts() {
        return ApiResponse.ok("Accounts fetched", accountService.getAllAccounts());
    }

    @GetMapping("/{accountNo}")
    public ApiResponse<Account> getAccount(@PathVariable Long accountNo) {
        return ApiResponse.ok("Account fetched", accountService.getAccount(accountNo));
    }
}
