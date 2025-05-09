package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.Proveedor;
import com.tienda.tiendaropa.service.FirebaseService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public String saveProveedor(HttpServletRequest request, @Valid @RequestBody Proveedor proveedor) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo administradores pueden crear proveedores");
        }

        return firebaseService.saveProveedor(proveedor);
    }

    @GetMapping("/{id}")
    public Proveedor getProveedorById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return firebaseService.getProveedorById(id);
    }

    @GetMapping
    public List<Proveedor> getAllProveedores() throws ExecutionException, InterruptedException {
        return firebaseService.getAllProveedores();
    }

    @DeleteMapping("/{id}")
    public String deleteProveedorById(HttpServletRequest request, @PathVariable String id) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo administradores pueden eliminar proveedores");
        }

        return firebaseService.deleteProveedor(id);
    }
}
