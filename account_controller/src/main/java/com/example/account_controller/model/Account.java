package com.example.account_controller.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

    @Entity
    @Table(name = "accounts")
    public class Account {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "client_id", nullable = false)
        private Long clientId;

        @Column(nullable = false)
        private BigDecimal balance;

        @Column(nullable = false)
        private String currency;

        // геттеры/сеттеры

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
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

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

    }


