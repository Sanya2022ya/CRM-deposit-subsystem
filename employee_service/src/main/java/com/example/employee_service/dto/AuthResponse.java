// AuthResponse.java
package com.example.employee_service.dto;

public class AuthResponse {
    private String jwtToken;

    public AuthResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    // Getter
    public String getJwtToken() {
        return jwtToken;
    }
}