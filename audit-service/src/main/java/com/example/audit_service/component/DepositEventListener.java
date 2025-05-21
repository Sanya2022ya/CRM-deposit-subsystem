package com.example.audit_service.component;

import com.example.audit_service.model.DepositAuditLog;
import com.example.common_dto.DepositEvent;
import com.example.audit_service.repository.DepositAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DepositEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DepositEventListener.class);
    private final DepositAuditRepository repository;

    public DepositEventListener(DepositAuditRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "deposit-events",
            groupId = "audit-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(DepositEvent event) {
        logger.info("Получено сообщение: {}", event);

        if (repository.existsByEventId(event.getEventId())) {
            logger.info("Событие уже обработано, пропускаем: {}", event.getEventId());
            return;
        }

        DepositAuditLog log = new DepositAuditLog();
        log.setEventId(event.getEventId()); //
        log.setClientId(event.getClientId());
        log.setDepositId(event.getDepositId());
        log.setAmount(event.getAmount());
        log.setOperation(event.getOperation());
        log.setTimestamp(event.getTimestamp());

        try {
            repository.save(log);
            logger.info("Сообщение сохранено в аудит-лог: {}", log);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Попытка сохранить дубликат по eventId: {}", event.getEventId());
        }
    }
}
