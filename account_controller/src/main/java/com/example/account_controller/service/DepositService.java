package com.example.account_controller.service;

import com.example.common_dto.DepositEvent;
import com.example.account_controller.model.Account;
import com.example.account_controller.model.ClientDeposit;
import com.example.account_controller.model.DepositType;
import com.example.account_controller.repository.AccountRepository;
import com.example.account_controller.repository.ClientDepositRepository;
import com.example.account_controller.repository.DepositTypeRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
@Transactional
public class DepositService {

    private final AccountRepository accountRepo;
    private final DepositTypeRepository typeRepo;
    private final DepositTypeRepository depositTypeRepo;
    private final ClientDepositRepository depositRepo;
    private final KafkaTemplate<String, DepositEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(DepositService.class);
    public DepositService(AccountRepository accountRepo,DepositTypeRepository typeRepo,
                          DepositTypeRepository depositTypeRepo,
                          ClientDepositRepository depositRepo,
                          KafkaTemplate<String, DepositEvent> kafkaTemplate) {
        this.accountRepo = accountRepo;
        this.typeRepo = typeRepo;
        this.depositTypeRepo = depositTypeRepo;
        this.depositRepo = depositRepo;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Transactional(isolation = REPEATABLE_READ)
    public void topUpDeposit(Long clientId, Long depositId, BigDecimal amount) {
        ClientDeposit deposit = depositRepo.findByIdAndClientIdAndClosedFalse(depositId, clientId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found or closed"));

        if (!deposit.getDepositType().isAllowTopUp()) {
            throw new IllegalStateException("Top-up is not allowed for this deposit type");
        }

        Account account = accountRepo.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough funds in account");
        }

        account.setBalance(account.getBalance().subtract(amount));
        deposit.setAmount(deposit.getAmount().add(amount));

        accountRepo.save(account);
        depositRepo.save(deposit);


        DepositEvent event = new DepositEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setClientId(clientId);
        event.setDepositId(depositId);
        event.setAmount(amount);
        event.setOperation("TOP_UP");
        event.setTimestamp(java.time.LocalDateTime.now());

        log.info("Отправка события в Kafka: {}", event);
        kafkaTemplate.send("deposit-events", event);
    }

    @Transactional(isolation = REPEATABLE_READ)
    public void withdrawFromDeposit(Long clientId, Long depositId, BigDecimal amount) {
        ClientDeposit deposit = depositRepo.findByIdAndClientIdAndClosedFalse(depositId, clientId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found or closed"));

        if (!deposit.getDepositType().isAllowWithdrawal()) {
            throw new IllegalStateException("Withdrawals not allowed for this deposit type");
        }

        if (deposit.getAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough funds in deposit");
        }

        Account account = accountRepo.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        deposit.setAmount(deposit.getAmount().subtract(amount));
        account.setBalance(account.getBalance().add(amount));

        depositRepo.save(deposit);
        accountRepo.save(account);

        // Создание и отправка события
        DepositEvent event = new DepositEvent();
        event.setClientId(clientId);
        event.setDepositId(depositId);
        event.setAmount(amount);
        event.setOperation("WITHDRAW"); // ключевая разница
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send("deposit-events", event);
        log.info("Sent deposit withdrawal event: {}", event);
    }

    @Transactional(isolation = REPEATABLE_READ)
    public ClientDeposit openDeposit(Long clientId, Long depositTypeId, BigDecimal amount) {
        DepositType type = depositTypeRepo.findById(depositTypeId)
                .orElseThrow(() -> new RuntimeException("Deposit type not found"));

        if (amount.compareTo(type.getMinAmount()) < 0)
            throw new IllegalArgumentException("Amount below minimum");

        Account account = accountRepo.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds");

        // списываем деньги
        account.setBalance(account.getBalance().subtract(amount));
        accountRepo.save(account);

        // создаём вклад
        ClientDeposit deposit = new ClientDeposit();
        deposit.setClientId(clientId);
        deposit.setDepositType(type);
        deposit.setAmount(amount);
        deposit.setOpenDate(LocalDate.now());
        deposit.setClosed(false);
        deposit = depositRepo.save(deposit); // сохраняем и получаем ID

        // отправка события открытия вклада
        DepositEvent event = new DepositEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setClientId(clientId);
        event.setDepositId(deposit.getId());
        event.setAmount(amount);
        event.setOperation("OPEN");
        event.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send("deposit-events", event);
        log.info("Sent deposit open event: {}", event);

        return deposit;
    }


    @Transactional(isolation = REPEATABLE_READ)
    public void closeDeposit(Long depositId) {
        ClientDeposit deposit = this.depositRepo.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Deposit not found with ID: " + depositId));

        if (deposit.isClosed()) {
            throw new IllegalStateException("Deposit with ID: " + depositId + " is already closed.");
        }

        if (!deposit.getDepositType().isAllowAutoClose()) {
            Integer termMonths = deposit.getDepositType().getTermMonths();
            if (termMonths != null && deposit.getOpenDate() != null) {
                LocalDate expectedCloseDate = deposit.getOpenDate().plusMonths(termMonths);
                if (LocalDate.now().isBefore(expectedCloseDate)) {
                    throw new IllegalStateException("Этот вклад нельзя закрыть до конца срока действия ");
                }
            }
        }


        // Закрываем вклад
        deposit.setClosed(true);
        deposit.setCloseDate(LocalDate.now());

        // Возвращаем деньги на счёт
        Long clientId = deposit.getClientId();
        Account clientAccount = this.accountRepo.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client account not found for deposit ID: " + depositId));

        BigDecimal depositAmountToReturn = deposit.getAmount();
        BigDecimal newBalance = clientAccount.getBalance().add(depositAmountToReturn);
        clientAccount.setBalance(newBalance);

        this.accountRepo.save(clientAccount);

        // Обнуляем сумму вклада
        deposit.setAmount(BigDecimal.ZERO);
        this.depositRepo.save(deposit);

        // Событие
        DepositEvent event = new DepositEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setClientId(clientId);
        event.setDepositId(depositId);
        event.setAmount(depositAmountToReturn);
        event.setOperation("CLOSE");
        event.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send("deposit-events", event);
        log.info("Sent deposit close event: {}", event);
    }




}

