package com.example.account_controller.controller;

import com.example.account_controller.model.Account;
import com.example.account_controller.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // Получение баланса по clientId
    @GetMapping("/balance/{clientId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long clientId) {
        Account account = service.getByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return ResponseEntity.ok(account.getBalance());
    }

    // Создание счёта
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAccount(account));
    }

    // Пополнение счёта
    @PostMapping("/{id}/top-up")
    public ResponseEntity<Account> topUp(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            return ResponseEntity.ok(service.topUp(id, amount));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    // Снятие средств
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            return ResponseEntity.ok(service.withdraw(id, amount));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    // Удаление счёта
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Account> getAccountByClientId(@PathVariable Long clientId) {
        Account account = service.getByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return ResponseEntity.ok(account);
    }

}
