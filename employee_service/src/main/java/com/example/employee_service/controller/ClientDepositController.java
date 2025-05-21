package com.example.employee_service.controller;

import com.example.employee_service.dto.DepositResponse;
import com.example.employee_service.dto.DepositTypeDto;
import com.example.employee_service.dto.OpenDepositRequest;
import com.example.employee_service.model.DepositType;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clients/{clientId}/deposits")
public class ClientDepositController {

    private final RestTemplate restTemplate;

    public ClientDepositController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    @GetMapping("/client-profile")
    public String clientProfile(@PathVariable("clientId") Long clientId, Model model){

        // 1. Получаем шаблоны вкладов
        String typesUrl = "http://localhost:8082/api/depositstypes/";
        ResponseEntity<DepositTypeDto[]> response = restTemplate.getForEntity(typesUrl, DepositTypeDto[].class);
        DepositTypeDto[] depositTypes = response.getBody();
        model.addAttribute("depositTypes", depositTypes);

        // 2. Получаем вклады клиента
        String depositsUrl = "http://localhost:8082/api/deposits/client/{clientId}";
        ResponseEntity<DepositResponse[]> depositsResponse = restTemplate.getForEntity(depositsUrl, DepositResponse[].class);
        DepositResponse[] deposits = depositsResponse.getBody();
        model.addAttribute("deposits", deposits);

        model.addAttribute("clientId", clientId);

        return "client-profile";
    }
    @PostMapping("/open")
    public String openDeposit(@PathVariable Long clientId,
                              @RequestParam Long depositTypeId,
                              @RequestParam Double amount,
                              RedirectAttributes redirectAttributes) {
        String depositServiceUrl = "http://localhost:8082/api/deposits/open";

        OpenDepositRequest request = new OpenDepositRequest(clientId, depositTypeId, amount);

        try {
            ResponseEntity<DepositResponse> response = restTemplate.postForEntity(
                    depositServiceUrl, request, DepositResponse.class
            );

            redirectAttributes.addFlashAttribute("message", "Вклад успешно открыт");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при открытии вклада: " + e.getMessage());
        }

        return "redirect:/client-profile/" + clientId;
    }
    @PostMapping("/top-up")
    public String topUpDeposit(@PathVariable Long clientId,
                               @RequestParam Long depositId,
                               @RequestParam Double amount,
                               RedirectAttributes redirectAttributes) {
        String url = "http://localhost:8082/api/deposits/top-up";

        // JSON запрос
        var request = new java.util.HashMap<String, Object>();
        request.put("clientId", clientId);
        request.put("depositId", depositId);
        request.put("amount", amount);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            redirectAttributes.addFlashAttribute("message", "Вклад успешно пополнен");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при пополнении вклада: " + e.getMessage());
        }

        return "redirect:/client-profile/" + clientId;
    }
    @PostMapping("/withdraw")
    public String withdrawFromDeposit(@PathVariable Long clientId,
                                      @RequestParam Long depositId,
                                      @RequestParam Double amount,
                                      RedirectAttributes redirectAttributes) {
        String url = "http://localhost:8082/api/deposits/withdraw";

        // JSON тело запроса
        var request = new java.util.HashMap<String, Object>();
        request.put("clientId", clientId);
        request.put("depositId", depositId);
        request.put("amount", amount);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            redirectAttributes.addFlashAttribute("message", "Снятие со вклада успешно выполнено");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при снятии со вклада: " + e.getMessage());
        }

        return "redirect:/client-profile/" + clientId;
    }
    @PostMapping("/{depositId}/close")
    public String closeDeposit(@PathVariable Long clientId,
                               @PathVariable Long depositId,
                               RedirectAttributes redirectAttributes) {
        String url = "http://localhost:8082/api/deposits/" + depositId + "/close";

        try {
            restTemplate.postForEntity(url, null, Void.class);
            redirectAttributes.addFlashAttribute("message", "Вклад успешно закрыт");
        } catch (RestClientResponseException e) {
            // Вытаскиваем тело ответа с текстом ошибки
            redirectAttributes.addFlashAttribute("error", e.getResponseBodyAsString());
        } catch (RestClientException e) {
            // Общая ошибка, если вдруг не попали в предыдущий catch
            redirectAttributes.addFlashAttribute("error", "Ошибка при закрытии вклада.");
        }

        return "redirect:/client-profile/" + clientId;
    }

}
