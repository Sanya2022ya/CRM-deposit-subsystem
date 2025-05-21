package com.example.account_controller.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "deposit_type")
public class DepositType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "term_months")
    private Integer termMonths;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "allow_top_up")
    private boolean allowTopUp;

    @Column(name = "allow_withdrawal")
    private boolean allowWithdrawal;

    @Column(name = "auto_prolongation")
    private boolean autoProlongation;
    @Column(name = "allow_auto_close")
    private boolean allowAutoClose;

    // геттеры/сеттеры

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public boolean isAllowTopUp() {
        return allowTopUp;
    }

    public void setAllowTopUp(boolean allowTopUp) {
        this.allowTopUp = allowTopUp;
    }

    public boolean isAllowWithdrawal() {
        return allowWithdrawal;
    }

    public void setAllowWithdrawal(boolean allowWithdrawal) {
        this.allowWithdrawal = allowWithdrawal;
    }

    public boolean isAutoProlongation() {
        return autoProlongation;
    }

    public void setAutoProlongation(boolean autoProlongation) {
        this.autoProlongation = autoProlongation;
    }

    public boolean isAllowAutoClose() {return allowAutoClose;}

    public void setAutoClose(boolean allowAutoClose) {this.allowAutoClose = allowAutoClose;}
}

