package com.shop.payments.repository;

import com.shop.payments.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccountIdAndCreatedAtAfter(
            UUID accountId,
            LocalDateTime fromDate
    );
}