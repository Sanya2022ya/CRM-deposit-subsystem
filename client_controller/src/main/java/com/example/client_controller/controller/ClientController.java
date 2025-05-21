package com.example.client_controller.controller;

import com.example.client_controller.model.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.client_controller.service.ClientService;
@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/passport/{passport}")
    public ResponseEntity<Client> getClientByPassport(@PathVariable String passport) {
        return service.getByPassport(passport)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @ResponseBody
    @GetMapping("/search-any")
    public List<Client> searchByAny(@RequestParam String query) {
        return service.searchByAnyFioPart(query);
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return service.create(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        return service.update(id, client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{id}/block")
    public String blockClient(@PathVariable Long id) {
        service.block(id);
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/unblock")
    public String unblockClient(@PathVariable Long id) {
        service.unblock(id);
        return "redirect:/clients/" + id;
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Client> restoreClient(@PathVariable Long id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/{id}/remove")
    public String removeClient(@PathVariable Long id) {
        service.delete(id); // Можно игнорировать Optional, если вы уверены, что id валиден
        return "redirect:/clients"; // Перенаправляем, например, на список клиентов
    }

    @GetMapping("/clients")
    public String listClients(Model model) {
        List<Client> clients = service.getAll();
        model.addAttribute("clients", clients);
        model.addAttribute("newClient", new Client()); // для модального окна
        return "clients";
    }

}

