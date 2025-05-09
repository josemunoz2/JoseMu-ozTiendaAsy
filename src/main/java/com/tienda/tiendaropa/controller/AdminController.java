package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.CompraStockRequest;
import com.tienda.tiendaropa.service.FirebaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/comprar-stock")
    public String comprarStock(HttpServletRequest request, @Valid @RequestBody CompraStockRequest stockRequest) throws ExecutionException, InterruptedException {

        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo los administradores pueden agregar stock");
        }

        return firebaseService.aumentarStockProveedor(
                stockRequest.getProductoId(),
                stockRequest.getProveedorId(),
                stockRequest.getCantidad()
        );
    }
}
