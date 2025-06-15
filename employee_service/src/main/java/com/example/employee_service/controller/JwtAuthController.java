package com.example.employee_service.controller;

import com.example.employee_service.config.JwtUtil;
import com.example.employee_service.dto.AuthRequest;
import com.example.employee_service.model.Employee;
import com.example.employee_service.repository.EmployeeRepository;
import com.example.employee_service.service.EmployeeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmployeeDetailsService employeeDetailsService;
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndSetCookie(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = employeeDetailsService.loadUserByUsername(authRequest.getUsername());
                String token = jwtUtil.generateToken(userDetails);

                Cookie jwtCookie = new Cookie("jwtToken", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                // jwtCookie.setSecure(true);
                jwtCookie.setMaxAge((int) (jwtUtil.getExpiration() / 1000));

                response.addCookie(jwtCookie);

                return ResponseEntity.ok("Authentication successful, JWT token set in HttpOnly cookie.");
            } else {
                throw new BadCredentialsException("Неверное имя пользователя или пароль.");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка аутентификации: " + e.getMessage());
        }
    }

    @GetMapping("/api/user/role")
    public ResponseEntity<String> getUserRole(Principal principal) {
        if (principal != null) {
            Employee employee = employeeRepository.findByUsername(principal.getName()).orElse(null);
            if (employee != null) {
                return ResponseEntity.ok(employee.getRole());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtToken", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok("Выход выполнен успешно.");
    }
}