package com.example.account_controller.dto;


import java.math.BigDecimal;

public class TransferRequest {
    public Long clientId;
    public Long depositId;
    public BigDecimal amount;
}
