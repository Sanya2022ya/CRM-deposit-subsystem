package com.example.employee_service.dto;

import com.example.employee_service.model.ClientDeposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepositResponse {

    private static final Logger logger = LoggerFactory.getLogger(DepositResponse.class);

    private Long id;
    private Long clientId;
    private String depositType;
    private BigDecimal amount;
    private LocalDate openDate;
    private boolean isClosed;
    private LocalDate closeDate;

    public static DepositResponse fromEntity(ClientDeposit d) {
        DepositResponse res = new DepositResponse();
        res.setId(d.getId());
        res.setClientId(d.getClientId());
        res.setDepositType(d.getDepositType() != null ? d.getDepositType().getName() : null);
        res.setAmount(d.getAmount());
        res.setOpenDate(d.getOpenDate());
        res.setClosed(d.isClosed());

        if (d.getOpenDate() != null && d.getDepositType() != null && d.getDepositType().getTermMonths() != null) {
            res.setCloseDate(d.getOpenDate().plusMonths(d.getDepositType().getTermMonths()));
            logger.info("{}", d.getDepositType());
        } else {
            logger.warn("Невозможно рассчитать closeDate для депозита с id {}: openDate = {}, depositType = {}, termMonths = {}",
                    d.getId(),
                    d.getOpenDate(),
                    d.getDepositType(),
                    (d.getDepositType() != null ? d.getDepositType().getTermMonths() : "null"));
        }

        return res;
    }

    @Override
    public String toString() {
        return "DepositResponse{" +
                "id=" + id +
                ", amount=" + amount +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", isClosed=" + isClosed +
                '}';
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }
}
