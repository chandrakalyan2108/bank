package com.bank.app.repository;

import com.bank.app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByStatus(String status);
    List<Account> findByCustomerId(Long customerId);
}
