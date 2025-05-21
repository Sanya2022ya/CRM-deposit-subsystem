package com.example.account_controller.repository;
import com.example.account_controller.model.DepositType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositTypeRepository extends JpaRepository<DepositType, Long> {
}
