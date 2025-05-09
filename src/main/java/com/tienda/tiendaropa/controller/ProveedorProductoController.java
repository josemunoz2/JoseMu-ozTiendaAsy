package com.tienda.tiendaropa.controller;

import com.google.firebase.auth.FirebaseToken;
import com.tienda.tiendaropa.model.ProveedorProducto;
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
@RequestMapping("/proveedor-producto")
public class ProveedorProductoController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public String create(HttpServletRequest request, @Valid @RequestBody ProveedorProducto proveedorProducto) throws ExecutionException, InterruptedException {
        validarAdmin(request);
        return firebaseService.saveProveedorProducto(proveedorProducto);
    }

    @GetMapping
    public List<ProveedorProducto> getAll() throws ExecutionException, InterruptedException {
        return firebaseService.getAllProveedorProductos();
    }

    @GetMapping("/{id}")
    public ProveedorProducto getById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return firebaseService.getProveedorProductoById(id);
    }

    @PutMapping("/{id}")
    public String update(HttpServletRequest request, @PathVariable String id, @Valid @RequestBody ProveedorProducto proveedorProducto) throws ExecutionException, InterruptedException {
        validarAdmin(request);
        proveedorProducto.setId(id);
        return firebaseService.updateProveedorProducto(proveedorProducto);
    }

    @DeleteMapping("/{id}")
    public String delete(HttpServletRequest request, @PathVariable String id) throws ExecutionException, InterruptedException {
        validarAdmin(request);
        return firebaseService.deleteProveedorProducto(id);
    }

    private void validarAdmin(HttpServletRequest request) {
        FirebaseToken user = (FirebaseToken) request.getAttribute("firebaseUser");
        if (user == null || !user.getEmail().equals("admin@correo.com")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo administradores pueden realizar esta operación");
        }
    }
}
