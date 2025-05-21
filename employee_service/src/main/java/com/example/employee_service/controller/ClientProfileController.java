package com.example.employee_service.controller;

import com.example.employee_service.dto.ClientDTO;
import com.example.employee_service.dto.DepositResponse;
import com.example.employee_service.dto.DepositTypeDto;
import com.example.employee_service.service.ClientProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class ClientProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ClientProfileController.class);

    private final RestTemplate restTemplate;
    private final ClientProfileService profileService;

    public ClientProfileController(RestTemplate restTemplate, ClientProfileService profileService) {
        this.restTemplate = restTemplate;
        this.profileService = profileService;
    }

    @GetMapping("/client-profile/{id}")
    public String viewClientProfile(@PathVariable Long id, Model model) {
        String clientUrl = "http://localhost:8081/clients/" + id;
        String depositTypesUrl = "http://localhost:8082/api/depositstypes/";

        ClientDTO client = restTemplate.getForObject(clientUrl, ClientDTO.class);
        BigDecimal balance = profileService.getBalance(id);
        List<DepositResponse> deposits = profileService.getDeposits(id);

        // 👇 Загрузка типов вкладов
        DepositTypeDto[] depositTypes = restTemplate.getForObject(depositTypesUrl, DepositTypeDto[].class);

        // 👇 Передаём всё в модель
        model.addAttribute("client", client);
        model.addAttribute("balance", balance);
        model.addAttribute("deposits", deposits);
        model.addAttribute("depositTypes", depositTypes); // <<< вот это важно

        return "client-profile";
    }

}

