package com.example.account_controller.repository;
import com.example.account_controller.model.ClientDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientDepositRepository extends JpaRepository<ClientDeposit, Long> {
    List<ClientDeposit> findByClientIdAndClosedFalse(Long clientId);
    Page<ClientDeposit> findByClosedFalse(Pageable pageable);
    Optional<ClientDeposit> findByIdAndClientIdAndClosedFalse(Long id, Long clientId);
}
