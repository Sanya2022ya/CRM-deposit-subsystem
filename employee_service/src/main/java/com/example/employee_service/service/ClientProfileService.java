package com.example.employee_service.service;

import com.example.employee_service.dto.DepositResponse;
import com.example.employee_service.model.DepositType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ClientProfileService {
    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://localhost:8082/api";

    public ClientProfileService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getBalance(Long clientId) {
        String url = accountServiceUrl + "/accounts/balance/" + clientId;
        try {
            return restTemplate.getForObject(url, BigDecimal.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null; // аккаунт не найден
        }
    }

    public List<DepositResponse> getDeposits(Long clientId) {
        String url = accountServiceUrl + "/deposits/client/" + clientId;
        try {
            ResponseEntity<DepositResponse[]> response = restTemplate.getForEntity(url, DepositResponse[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList(); // депозитов нет
        }
    }

    public DepositType getDepositType(Long typeId) {
        String url = accountServiceUrl + "/depositstypes/" + typeId;
        return restTemplate.getForObject(url, DepositType.class);
    }
}
