package com.shop.payments.service;

import com.shop.payments.domain.model.Account;
import com.shop.payments.domain.model.Transaction;
import com.shop.payments.repository.AccountRepository;
import com.shop.payments.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Account createAccount(UUID userId) {
        if (accountRepository.existsByUserId(userId)) {
            throw new AccountAlreadyExistsException("Account already exists for user: " + userId);
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public Account deposit(UUID userId, BigDecimal amount) {
        Account account = getAccount(userId);
        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);
        createTransaction(account.getId(), Transaction.TransactionType.DEPOSIT, amount);
        return updatedAccount;
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public Account withdraw(UUID userId, BigDecimal amount) {
        Account account = getAccount(userId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepository.save(account);
        createTransaction(account.getId(), Transaction.TransactionType.WITHDRAWAL, amount);
        return updatedAccount;
    }

    public BigDecimal getBalance(UUID userId) {
        return getAccount(userId).getBalance();
    }

    public List<Transaction> getTransactionHistory(UUID userId, int days) {
        Account account = getAccount(userId);
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return transactionRepository.findByAccountIdAndCreatedAtAfter(
                account.getId(), fromDate
        );
    }

    private Account getAccount(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
    }

    private void createTransaction(UUID accountId, Transaction.TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transactionRepository.save(transaction);
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class AccountAlreadyExistsException extends RuntimeException {
        public AccountAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
}