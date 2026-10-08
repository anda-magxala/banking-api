package com.banking.api.service;

import com.banking.api.model.Account;
import com.banking.api.repository.AccountRepository;
import com.banking.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public BigDecimal getBalance(String accountNumber) {
        return getAccount(accountNumber).getBalance();
    }

    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        Account acc = getAccount(accountNumber);
        acc.setBalance(acc.getBalance().add(amount));
        return accountRepository.save(acc);
    }

    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        Account acc = getAccount(accountNumber);
        if (acc.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient funds");
        acc.setBalance(acc.getBalance().subtract(amount));
        return accountRepository.save(acc);
    }

    @Transactional
    public void transfer(String from, String to, BigDecimal amount) {
        Account fromAcc = getAccount(from);
        Account toAcc = getAccount(to);
        if (fromAcc.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient funds");
        fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
        toAcc.setBalance(toAcc.getBalance().add(amount));
        accountRepository.save(fromAcc);
        accountRepository.save(toAcc);
    }
}
