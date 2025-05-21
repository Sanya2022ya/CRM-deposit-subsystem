package com.example.account_controller.repository;

import com.example.account_controller.model.Account;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository; // Или другой репозиторий

import java.util.Optional;

@Repository // Убедитесь, что эта аннотация присутствует
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Ваш метод для поиска по client ID
    Optional<Account> findByClientId(Long clientId); // Убедитесь, что сигнатура метода правильная
}
