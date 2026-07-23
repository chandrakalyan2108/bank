package com.bank.app.controller;

import com.bank.app.dto.ApiResponse;
import com.bank.app.dto.TransactionRequest;
import com.bank.app.entity.Transaction;
import com.bank.app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ApiResponse<Transaction> deposit(@Valid @RequestBody TransactionRequest request) {
        return ApiResponse.ok("Deposit successful", transactionService.deposit(request, null));
    }

    @PostMapping("/withdraw")
    public ApiResponse<Transaction> withdraw(@Valid @RequestBody TransactionRequest request) {
        return ApiResponse.ok("Withdrawal successful", transactionService.withdraw(request, null));
    }

    @GetMapping("/{accountNo}/history")
    public ApiResponse<List<Transaction>> history(@PathVariable Long accountNo) {
        return ApiResponse.ok("Transaction history fetched", transactionService.getHistory(accountNo));
    }
}
