package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.Transaction;
import com.quickpos.quickposbackend.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepo.findById(id);
    }

    public Transaction updateTransaction(Long id, Transaction newData) {
        return transactionRepo.findById(id)
                .map(existing -> {
                    existing.setTimestamp(newData.getTimestamp());
                    existing.setTotalAmount(newData.getTotalAmount());
                    existing.setEmployee(newData.getEmployee());
                    existing.setPaymentMethod(newData.getPaymentMethod());
                    existing.setStatus(newData.getStatus());
                    return transactionRepo.save(existing);
                }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void deleteTransaction(Long id) {
        transactionRepo.deleteById(id);
    }

}

