package com.example.client_controller.service;

import com.example.client_controller.model.Client;
import com.example.client_controller.model.ClientStatus;
import com.example.client_controller.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;

    @Autowired
    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> getAll() {
        return repository.findByStatusNot(ClientStatus.DELETED);
    }


    public Optional<Client> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<Client> getByPassport(String passport) {
        return repository.findByPassport(passport);
    }

    public List<Client> searchByAnyFioPart(String query) {
        return repository.searchByAnyFioPart(query);
    }

    public Client create(Client client) {
        client.setStatus(ClientStatus.ACTIVE); // Было: активен
        return repository.save(client);
    }

    public Optional<Client> update(Long id, Client updatedClient) {
        return repository.findById(id).map(client -> {
            client.setFirstName(updatedClient.getFirstName());
            client.setLastName(updatedClient.getLastName());
            client.setMiddleName(updatedClient.getMiddleName());
            client.setBirthDate(updatedClient.getBirthDate());
            client.setGender(updatedClient.getGender());
            client.setContactInfo(updatedClient.getContactInfo());
            client.setPassport(updatedClient.getPassport());
            client.setAddress(updatedClient.getAddress());
            client.setStatus(updatedClient.getStatus()); // Уже Enum
            return repository.save(client);
        });
    }

    public Optional<Client> delete(Long id) {
        return repository.findById(id).map(client -> {
            client.setStatus(ClientStatus.DELETED); // Было: удален
            return repository.save(client);
        });
    }

    public Optional<Client> restore(Long id) {
        return repository.findById(id).map(client -> {
            client.setStatus(ClientStatus.ACTIVE); // Было: активен
            return repository.save(client);
        });
    }

    public Optional<Client> block(Long id) {
        return repository.findById(id).map(client -> {
            client.setStatus(ClientStatus.BLOCKED); // Было: заблокирован
            return repository.save(client);
        });
    }

    public Optional<Client> unblock(Long id) {
        return repository.findById(id).map(client -> {
            client.setStatus(ClientStatus.ACTIVE);
            return repository.save(client);
        });
    }
}
