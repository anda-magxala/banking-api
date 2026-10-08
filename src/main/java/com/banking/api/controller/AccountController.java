package com.banking.api.controller;

import com.banking.api.dto.TransactionRequest;
import com.banking.api.model.Account;
import com.banking.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> balance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(Map.of("accountNumber", accountNumber, "balance", accountService.getBalance(accountNumber)));
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
