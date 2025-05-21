package com.example.employee_service.dto;

public class OpenDepositRequest {
    public Long clientId;
    public Long depositTypeId;
    public Double amount;

    public OpenDepositRequest(Long clientId, Long depositTypeId, Double amount) {
        this.clientId = clientId;
        this.depositTypeId = depositTypeId;
        this.amount = amount;
    }

    public OpenDepositRequest() {}
}
