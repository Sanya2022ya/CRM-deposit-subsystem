package com.example.account_controller.controller;


import com.example.account_controller.dto.DepositResponse;
import com.example.account_controller.dto.OpenDepositRequest;
import com.example.account_controller.dto.TransferRequest;
import com.example.account_controller.model.Account;
import com.example.account_controller.model.ClientDeposit;
import com.example.account_controller.model.DepositType;
import com.example.account_controller.repository.AccountRepository;
import com.example.account_controller.repository.ClientDepositRepository;
import com.example.account_controller.service.DepositService;
import com.example.account_controller.service.DepositTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/depositstypes")
public class DepositTypeController {

    private final DepositTypeService depositTypeService;
    private final ClientDepositRepository depositRepo;

    public DepositTypeController(DepositTypeService depositTypeService,
                             ClientDepositRepository depositRepo,
                             AccountRepository accountRepo) {
        this.depositTypeService = depositTypeService;
        this.depositRepo = depositRepo;
    }
    @GetMapping("/")
    public List<DepositType> getAllDepositTypes() {
        return depositTypeService.getAllDepositTypes();
    }

    // Получить шаблон по ID
    @GetMapping("/{id}")
    public ResponseEntity<DepositType> getDepositTypeById(@PathVariable Long id) {
        return depositTypeService.getDepositTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать новый шаблон вклада
    @PostMapping("/")
    public ResponseEntity<DepositType> createDepositType(@RequestBody DepositType type) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depositTypeService.createDepositType(type));
    }

    // Обновить шаблон вклада
    @PutMapping("/{id}")
    public ResponseEntity<DepositType> updateDepositType(@PathVariable Long id, @RequestBody DepositType updatedType) {
        return depositTypeService.updateDepositType(id, updatedType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Удалить шаблон вклада
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepositType(@PathVariable Long id) {
        if (depositTypeService.deleteDepositType(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}


