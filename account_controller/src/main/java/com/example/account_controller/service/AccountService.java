package com.example.account_controller.service;

import com.example.account_controller.model.Account;
import com.example.account_controller.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepo;

    public AccountService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    public Account createAccount(Account account) {
        account.setBalance(account.getBalance() == null ? BigDecimal.ZERO : account.getBalance());
        return accountRepo.save(account);
    }

    public Optional<Account> getByClientId(Long clientId) {
        return accountRepo.findByClientId(clientId);
    }

    @Transactional
    public Account topUp(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return account;
    }

    public void deleteAccount(Long accountId) {
        if (!accountRepo.existsById(accountId)) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepo.deleteById(accountId);
    }
}
