package com.bank.app.service;

import com.bank.app.dto.TransactionRequest;
import com.bank.app.entity.Account;
import com.bank.app.entity.Transaction;
import com.bank.app.exception.BadRequestException;
import com.bank.app.repository.AccountRepository;
import com.bank.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction deposit(TransactionRequest request, Long cashierId) {
        Account account = accountService.getAccount(request.getAccountNo());
        assertActive(account);

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        return transactionRepository.save(Transaction.builder()
                .accountNo(account.getAccountNo())
                .txnType("DEPOSIT")
                .amount(request.getAmount())
                .cashierId(cashierId)
                .build());
    }

    @Transactional
    public Transaction withdraw(TransactionRequest request, Long cashierId) {
        Account account = accountService.getAccount(request.getAccountNo());
        assertActive(account);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        return transactionRepository.save(Transaction.builder()
                .accountNo(account.getAccountNo())
                .txnType("WITHDRAW")
                .amount(request.getAmount())
                .cashierId(cashierId)
                .build());
    }

    public List<Transaction> getHistory(Long accountNo) {
        return transactionRepository.findByAccountNoOrderByTxnDateDesc(accountNo);
    }

    private void assertActive(Account account) {
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BadRequestException("Account is not ACTIVE. Current status: " + account.getStatus());
        }
    }
}
