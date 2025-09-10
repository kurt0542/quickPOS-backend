package com.quickpos.quickposbackend.repository;

import com.quickpos.quickposbackend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo  extends JpaRepository<Transaction, Long> {
}
