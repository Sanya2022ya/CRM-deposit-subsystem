package com.example.account_controller.dto;

import com.example.account_controller.model.ClientDeposit;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepositResponse {
    public Long id;
    public Long clientId;
    public String depositType;
    public BigDecimal amount;
    public LocalDate openDate;
    public boolean isClosed;
    public LocalDate closeDate;

    public static DepositResponse fromEntity(ClientDeposit d) {
        DepositResponse res = new DepositResponse();
        res.id = d.getId();
        res.clientId = d.getClientId();
        res.depositType = d.getDepositType().getName();
        res.amount = d.getAmount();
        res.openDate = d.getOpenDate();
        res.isClosed = d.isClosed();

        if (d.getOpenDate() != null && d.getDepositType() != null && d.getDepositType().getTermMonths() != null) {
            res.closeDate = d.getOpenDate().plusMonths(d.getDepositType().getTermMonths());
        }

        return res;
    }
}


