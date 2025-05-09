package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.Producto;
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
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public String saveProducto(HttpServletRequest request, @Valid @RequestBody Producto producto) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo administradores pueden crear productos");
        }

        return firebaseService.saveProducto(producto);
    }

    @DeleteMapping("/{id}")
    public String deleteProducto(HttpServletRequest request, @PathVariable String id) throws ExecutionException, InterruptedException {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");

        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo administradores pueden eliminar productos");
        }

        return firebaseService.deleteProducto(id);
    }

    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return firebaseService.getProductoById(id);
    }

    @GetMapping
    public List<Producto> filtrarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max
    ) throws ExecutionException, InterruptedException {
        return firebaseService.filtrarProductos(nombre, proveedor, min, max);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> getProductosPorCategoria(@PathVariable String categoria) throws ExecutionException, InterruptedException {
        return firebaseService.getProductosPorCategoria(categoria);
    }
}
