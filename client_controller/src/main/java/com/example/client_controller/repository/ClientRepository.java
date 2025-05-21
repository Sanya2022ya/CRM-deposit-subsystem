package com.example.client_controller.repository;
import com.example.client_controller.model.Client;
import com.example.client_controller.model.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByPassport(String passport);

    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.middleName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Client> searchByAnyFioPart(@Param("query") String query);
    List<Client> findByStatusNot(ClientStatus status); // Добавь этот метод

}

