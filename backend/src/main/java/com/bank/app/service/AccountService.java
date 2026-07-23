package com.bank.app.service;

import com.bank.app.dto.AccountRequest;
import com.bank.app.dto.ApprovalRequest;
import com.bank.app.entity.Account;
import com.bank.app.entity.Approval;
import com.bank.app.exception.BadRequestException;
import com.bank.app.exception.ResourceNotFoundException;
import com.bank.app.repository.AccountRepository;
import com.bank.app.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ApprovalRepository approvalRepository;

    public Account createAccount(AccountRequest request, Long createdBy) {
        Account account = Account.builder()
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .status("PENDING")
                .createdBy(createdBy)
                .build();
        return accountRepository.save(account);
    }

    public List<Account> getPendingAccounts() {
        return accountRepository.findByStatus("PENDING");
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccount(Long accountNo) {
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));
    }

    public Account approveAccount(Long accountNo, ApprovalRequest request, Long managerId) {
        Account account = getAccount(accountNo);
        if (!"PENDING".equals(account.getStatus())) {
            throw new BadRequestException("Account is not in PENDING state");
        }
        account.setStatus("ACTIVE");
        accountRepository.save(account);

        approvalRepository.save(Approval.builder()
                .accountNo(accountNo)
                .approvedBy(managerId)
                .status("APPROVED")
                .remarks(request.getRemarks())
                .build());

        return account;
    }

    public Account rejectAccount(Long accountNo, ApprovalRequest request, Long managerId) {
        Account account = getAccount(accountNo);
        if (!"PENDING".equals(account.getStatus())) {
            throw new BadRequestException("Account is not in PENDING state");
        }
        account.setStatus("REJECTED");
        accountRepository.save(account);

        approvalRepository.save(Approval.builder()
                .accountNo(accountNo)
                .approvedBy(managerId)
                .status("REJECTED")
                .remarks(request.getRemarks())
                .build());

        return account;
    }
}
