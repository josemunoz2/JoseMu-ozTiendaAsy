package com.tienda.tiendaropa.controller;

import com.tienda.tiendaropa.model.Carrito;
import com.tienda.tiendaropa.model.Carrito.ItemCarrito;
import com.tienda.tiendaropa.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam String clienteId, @RequestBody ItemCarrito item) throws ExecutionException, InterruptedException {
        if (clienteId == null || clienteId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es obligatorio");
        }
        return firebaseService.agregarProductoAlCarrito(clienteId, item);
    }

    @GetMapping
    public Carrito obtenerCarrito(@RequestParam String clienteId) throws ExecutionException, InterruptedException {
        if (clienteId == null || clienteId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es obligatorio");
        }
        return firebaseService.getCarritoByClienteId(clienteId);
    }

    @DeleteMapping
    public String eliminarCarrito(@RequestParam String clienteId) throws ExecutionException, InterruptedException {
        if (clienteId == null || clienteId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es obligatorio");
        }
        return firebaseService.eliminarCarrito(clienteId);
    }
}
