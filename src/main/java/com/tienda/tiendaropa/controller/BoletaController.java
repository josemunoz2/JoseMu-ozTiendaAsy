package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.Boleta;
import com.tienda.tiendaropa.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/boletas")
public class BoletaController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public String saveBoleta(HttpServletRequest request, @RequestBody Boleta boleta) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");
    
        if (user != null) {
            // Si está autenticado, usar UID
            boleta.setClienteId(user.getUid());
        } else {
            // Si no está autenticado, usar clienteId del body
            if (boleta.getClienteId() == null || boleta.getClienteId().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId requerido si no está autenticado");
            }
        }
    
        return firebaseService.saveBoleta("cliente", boleta);
    }
    

    @GetMapping
    public List<Boleta> getBoletas() throws ExecutionException, InterruptedException {
        return firebaseService.getAllBoletas()
                .stream()
                .map(doc -> doc.toObject(Boleta.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Boleta getBoletaById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return firebaseService.getBoletaById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteBoleta(@PathVariable String id) throws ExecutionException, InterruptedException {
        return firebaseService.deleteBoletaById(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Boleta> getBoletasByClienteId(HttpServletRequest request, @PathVariable String clienteId) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getUid().equals(clienteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para ver estas boletas");
        }

        return firebaseService.getBoletasByClienteId(clienteId)
                .stream()
                .map(doc -> doc.toObject(Boleta.class))
                .collect(Collectors.toList());
    }
}
