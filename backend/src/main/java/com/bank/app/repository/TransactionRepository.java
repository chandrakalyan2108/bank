package com.bank.app.repository;

import com.bank.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNoOrderByTxnDateDesc(Long accountNo);
}
