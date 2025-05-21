package com.example.employee_service.dto;

public class DepositTypeDto {
    private Long id;
    private String name;
    private Double interestRate;
    private Integer termMonths;
    private Double minAmount;
    private Boolean allowTopUp;
    private Boolean allowWithdrawal;
    private Boolean allowAutoClose;
    private Boolean autoProlongation;

    // геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Boolean getAllowTopUp() {
        return allowTopUp;
    }

    public void setAllowTopUp(Boolean allowTopUp) {
        this.allowTopUp = allowTopUp;
    }

    public Boolean getAllowWithdrawal() {
        return allowWithdrawal;
    }

    public void setAllowWithdrawal(Boolean allowWithdrawal) {
        this.allowWithdrawal = allowWithdrawal;
    }

    public Boolean getAllowAutoClose() {
        return allowAutoClose;
    }

    public void setAllowAutoClose(Boolean allowAutoClose) {
        this.allowAutoClose = allowAutoClose;
    }

    public Boolean getAutoProlongation() {
        return autoProlongation;
    }

    public void setAutoProlongation(Boolean autoProlongation) {
        this.autoProlongation = autoProlongation;
    }
}
