package com.example.employee_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlockedController {

    @GetMapping("/blocked")
    public String blockedPage() {
        return "blocked";  // имя Thymeleaf шаблона blocked.html в папке templates
    }
}
