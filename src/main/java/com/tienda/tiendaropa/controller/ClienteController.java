package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.Cliente;
import com.tienda.tiendaropa.service.FirebaseService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public String saveCliente(HttpServletRequest request, @Valid @RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");
        if (user != null) {
            cliente.setId(user.getUid());
        }
        return firebaseService.saveCliente(cliente);
    }

    @GetMapping
    public List<Cliente> getClientes(HttpServletRequest request) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");
        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso solo para administrador");
        }

        return firebaseService.getAllClientes()
                .stream()
                .map(doc -> doc.toObject(Cliente.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Cliente getClienteById(HttpServletRequest request, @PathVariable String id) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");
        if (user == null || !user.getUid().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        return firebaseService.getClienteById(id);
    }

    @PutMapping("/{id}")
public String updateCliente(@PathVariable String id, @Valid @RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
    if (id == null || id.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido");
    }

    cliente.setId(id);
    return firebaseService.saveCliente(cliente);
}

@DeleteMapping("/{id}")
public String deleteCliente(@PathVariable String id) throws ExecutionException, InterruptedException {
    if (id == null || id.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido");
    }

    return firebaseService.deleteClienteById(id);
}

}
