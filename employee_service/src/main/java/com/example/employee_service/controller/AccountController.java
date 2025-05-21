package com.example.employee_service.controller;

import com.example.employee_service.dto.AccountDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Controller
public class AccountController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ACCOUNT_SERVICE_URL = "http://localhost:8082/api"; // например, если account-service работает на порту 8082


    @PostMapping("/clients/{clientId}/top-up")
    public String topUpAccount(@PathVariable Long clientId,
                               @RequestParam BigDecimal amount) {
        // Сначала получаем ID счёта по clientId (можно из local DB или сделать GET в account-service)
        Long accountId = getAccountIdByClientId(clientId);
        if (accountId != null) {
            restTemplate.postForEntity(
                    ACCOUNT_SERVICE_URL + "/accounts/" + accountId + "/top-up?amount=" + amount,
                    null,
                    Void.class
            );
        }
        return "redirect:/clients/" + clientId;
    }

    @PostMapping("/clients/{clientId}/withdraw")
    public String withdrawFromAccount(@PathVariable Long clientId,
                                      @RequestParam BigDecimal amount) {
        Long accountId = getAccountIdByClientId(clientId);
        if (accountId != null) {
            restTemplate.postForEntity(
                    ACCOUNT_SERVICE_URL + "/accounts/" + accountId + "/withdraw?amount=" + amount,
                    null,
                    Void.class
            );
        }
        return "redirect:/clients/" + clientId;
    }

    private Long getAccountIdByClientId(Long clientId) {
        // Простой GET-запрос на микросервис для получения аккаунта
        ResponseEntity<AccountDTO> response = restTemplate.getForEntity(
                ACCOUNT_SERVICE_URL + "/accounts/by-client/" + clientId, AccountDTO.class
        );
        return response.getBody() != null ? response.getBody().getId() : null;
    }
}
