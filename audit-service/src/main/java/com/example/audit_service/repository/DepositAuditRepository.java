package com.example.audit_service.repository;

import com.example.audit_service.model.DepositAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositAuditRepository extends JpaRepository<DepositAuditLog, Long> {
    boolean existsByDepositId(Long depositId);
    boolean existsByEventId(String eventId);

}
