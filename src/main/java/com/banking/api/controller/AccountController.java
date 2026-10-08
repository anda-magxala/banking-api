package com.banking.api.controller;

import com.banking.api.dto.TransactionRequest;
import com.banking.api.model.Account;
import com.banking.api.repository.AccountRepository;
import com.banking.api.repository.UserRepository;
import com.banking.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<?> myAccounts(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(accountRepository.findByUserId(user.getId()));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> balance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(Map.of("accountNumber", accountNumber, "balance", accountService.getBalance(accountNumber)));
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<?> history(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getHistory(accountNumber));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionRequest req) {
        Account acc = accountService.deposit(req.getAccountNumber(), req.getAmount());
        return ResponseEntity.ok(Map.of("message", "Deposited", "newBalance", acc.getBalance()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionRequest req) {
        Account acc = accountService.withdraw(req.getAccountNumber(), req.getAmount());
        return ResponseEntity.ok(Map.of("message", "Withdrawn", "newBalance", acc.getBalance()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest req) {
        accountService.transfer(req.getAccountNumber(), req.getToAccountNumber(), req.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer successful from " + req.getAccountNumber() + " to " + req.getToAccountNumber()));
    }
}
