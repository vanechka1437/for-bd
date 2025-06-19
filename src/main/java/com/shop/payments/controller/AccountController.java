package com.shop.payments.controller;

import com.shop.payments.domain.model.Account;
import com.shop.payments.domain.model.Transaction;
import com.shop.payments.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(accountService.createAccount(userId));
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable UUID userId,
            @RequestParam BigDecimal amount
    ) {
        return ResponseEntity.ok(accountService.deposit(userId, amount));
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable UUID userId,
            @RequestParam BigDecimal amount
    ) {
        return ResponseEntity.ok(accountService.withdraw(userId, amount));
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID userId) {
        return ResponseEntity.ok(accountService.getBalance(userId));
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(accountService.getTransactionHistory(userId, days));
    }
}