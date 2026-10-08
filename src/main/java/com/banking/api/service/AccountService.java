package com.banking.api.service;

import com.banking.api.model.Account;
import com.banking.api.model.Transaction;
import com.banking.api.repository.AccountRepository;
import com.banking.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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
        accountRepository.save(acc);
        transactionRepository.save(new Transaction(null, accountNumber, "DEPOSIT", amount, "Deposit to " + accountNumber));
        return acc;
    }

    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        Account acc = getAccount(accountNumber);
        if (acc.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient funds");
        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);
        transactionRepository.save(new Transaction(accountNumber, null, "WITHDRAW", amount, "Withdraw from " + accountNumber));
        return acc;
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
        transactionRepository.save(new Transaction(from, to, "TRANSFER", amount, "Transfer from " + from + " to " + to));
    }

    public List<Transaction> getHistory(String accountNumber) {
        getAccount(accountNumber); // validate exists
        return transactionRepository.findByFromAccountOrToAccountOrderByTimestampDesc(accountNumber, accountNumber);
    }
}
