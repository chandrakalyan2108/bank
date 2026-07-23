package com.bank.app.repository;

import com.bank.app.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByAccountNo(Long accountNo);
}
