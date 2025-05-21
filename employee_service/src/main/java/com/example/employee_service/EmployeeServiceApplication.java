package com.example.employee_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
@EnableScheduling
@SpringBootApplication
public class EmployeeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeServiceApplication.class, args);
		try {
			// Открываем страницу входа в браузере
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/login");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

