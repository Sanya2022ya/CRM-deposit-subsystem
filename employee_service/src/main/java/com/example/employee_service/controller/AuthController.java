package com.example.employee_service.controller;



import com.example.employee_service.dto.AccountDTO;
import com.example.employee_service.dto.ClientDTO;
import com.example.employee_service.dto.DepositResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.employee_service.config.RestTemplateConfig;
import com.example.employee_service.model.Employee;
import com.example.employee_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/masterpage")
    public String masterpage(Model model) {
        List<Employee> employees = employeeRepository.findAll(); // получаем всех
        model.addAttribute("employees", employees); // именно ЭТО нужно для шаблона
        model.addAttribute("employee", new Employee());
        return "masterpage";
    }


    @PostMapping("/employees/{id}/toggle-block")
    public String toggleBlockEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employee.setBlocked(!employee.isBlocked()); // переключаем статус
            employeeRepository.save(employee);
        }
        return "redirect:/masterpage";
    }


    @PostMapping("/masterpage")
    public String register(@ModelAttribute Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        if (!employee.getRole().startsWith("ROLE_")) {
            employee.setRole("ROLE_" + employee.getRole());
        }
        employeeRepository.save(employee);
        return "redirect:/login";
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "query", required = false) String query,
                            Principal principal,
                            Model model) {
        model.addAttribute("username", principal.getName());

        List<ClientDTO> clients;

        if (query == null || query.trim().isEmpty()) {
            // Без фильтра — получаем всех
            ResponseEntity<List<ClientDTO>> response = restTemplate.exchange(
                    "http://localhost:8081/clients",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClientDTO>>() {}
            );
            clients = response.getBody();
        } else if (query.matches("\\d{4}\\s?\\d{6}")) {
            // Поиск по паспорту
            try {
                ClientDTO client = restTemplate.getForObject(
                        "http://localhost:8081/clients/passport/{passport}",
                        ClientDTO.class,
                        query.replaceAll("\\s", "")
                );
                clients = client != null ? List.of(client) : List.of();
            } catch (Exception e) {
                clients = List.of(); // если не найден — пустой список
            }
        } else {
            // Гибкий поиск по любой части ФИО
            ResponseEntity<List<ClientDTO>> response = restTemplate.exchange(
                    "http://localhost:8081/clients/search-any?query={query}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClientDTO>>() {},
                    query
            );
            clients = response.getBody();
        }


        model.addAttribute("clients", clients);
        model.addAttribute("query", query); // чтобы поле было заполнено после поиска
        return "dashboard";
    }

    @GetMapping("/clients/{id}")
    public String viewClient(@PathVariable Long id, Model model) {
        String url = "http://localhost:8081/clients/" + id;

        ClientDTO client = restTemplate.getForObject(url, ClientDTO.class);
        model.addAttribute("client", client);

        // если нужно, можешь добавить информацию о балансе, вкладах и т.д. аналогично
        return "client-profile"; // имя Thymeleaf-шаблона
    }
    @PostMapping("/clients/{id}")
    public String updateClient(@PathVariable Long id, @ModelAttribute ClientDTO client) {
        String url = "http://localhost:8081/clients/" + id;

        restTemplate.put(url, client);

        return "redirect:/clients/" + id;
    }

    @PostMapping("/clients/{id}/block")
    public String blockClient(@PathVariable Long id) {
        String url = "http://localhost:8081/clients/" + id + "/block";
        restTemplate.postForLocation(url, null);
        return "redirect:/clients/" + id;
    }

    @PostMapping("/clients/{id}/unblock")
    public String unblockClient(@PathVariable Long id) {
        String url = "http://localhost:8081/clients/" + id + "/unblock";
        restTemplate.postForLocation(url, null);
        return "redirect:/clients/" + id;
    }
    @PostMapping("/clients/{id}/remove")
    public String removeClient(@PathVariable Long id) {
        String url = "http://localhost:8081/clients/" + id + "/remove";
        restTemplate.postForLocation(url, null);
        return "redirect:/clients/" + id; // Перенаправляем, например, на список клиентов
    }
    @PostMapping("/clients/{id}/create-account")
    public String createAccount(@PathVariable Long id,
                                @RequestParam BigDecimal balance,
                                @RequestParam String currency) {
        AccountDTO account = new AccountDTO();
        account.setClientId(id);
        account.setBalance(balance);
        account.setCurrency(currency);

        restTemplate.postForEntity("http://localhost:8082/api/accounts", account, AccountDTO.class);
        return "redirect:/clients/" + id;
    }


}

