package com.example.account_controller.controller;

import com.example.account_controller.dto.DepositResponse;
import com.example.account_controller.dto.OpenDepositRequest;
import com.example.account_controller.dto.TransferRequest;
import com.example.account_controller.model.ClientDeposit;
import com.example.account_controller.repository.AccountRepository;
import com.example.account_controller.repository.ClientDepositRepository;
import com.example.account_controller.service.DepositService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    private final DepositService depositService;
    private final ClientDepositRepository depositRepo;
    private final AccountRepository accountRepo;

    public DepositController(DepositService depositService,
                             ClientDepositRepository depositRepo,
                             AccountRepository accountRepo) {
        this.depositService = depositService;
        this.depositRepo = depositRepo;
        this.accountRepo = accountRepo;
    }

    // Открытие нового вклада
    @PostMapping("/open")
    public ResponseEntity<DepositResponse> openDeposit(@RequestBody OpenDepositRequest req) {
        ClientDeposit deposit = depositService.openDeposit(req.clientId, req.depositTypeId, req.amount);
        return ResponseEntity.ok(DepositResponse.fromEntity(deposit));
    }

    // Получение всех вкладов клиента
    @GetMapping("/client/{clientId}")
    public List<DepositResponse> getDepositsByClient(@PathVariable Long clientId) {
        return depositRepo.findByClientIdAndClosedFalse(clientId).stream()
                .map(DepositResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/top-up")
    public ResponseEntity<String> topUpDeposit(@RequestBody TransferRequest req) {
        depositService.topUpDeposit(req.clientId, req.depositId, req.amount);
        return ResponseEntity.ok("Deposit topped up successfully");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawFromDeposit(@RequestBody TransferRequest req) {
        depositService.withdrawFromDeposit(req.clientId, req.depositId, req.amount);
        return ResponseEntity.ok("Funds withdrawn from deposit");
    }
    @PostMapping("/{depositId}/close") // Эндпоинт будет выглядеть как /api/deposits/{depositId}/close
    public ResponseEntity<String> closeDeposit(@PathVariable Long depositId) {
        try {
            // Вызываем метод сервиса, который уже содержит всю логику (проверка, обновление, сохранение)
            depositService.closeDeposit(depositId);
            // Возвращаем успешный ответ
            return ResponseEntity.ok("Deposit with ID " + depositId + " closed successfully");
        } catch (RuntimeException e) {
            // Обрабатываем ошибки, которые могут возникнуть в сервисе (например, вклад не найден, уже закрыт)
            // Возвращаем статус ошибки и сообщение из исключения
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            // Или другой подходящий статус, например, HttpStatus.NOT_FOUND если ошибка только о ненахождении вклада
        }
    }
}

