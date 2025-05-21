package com.example.account_controller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Укажите путь к вашим API эндпоинтам
                .allowedOrigins("http://localhost:8080") // Разрешенный источник
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешенные HTTP методы
                .allowedHeaders("*"); // Разрешенные заголовки запроса (можно уточнить)
        // .allowCredentials(true) // Если используются куки или авторизация
    }
}