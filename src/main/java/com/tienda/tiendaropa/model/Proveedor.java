package com.tienda.tiendaropa.model;

import jakarta.validation.constraints.NotBlank;

public class Proveedor {
    private String id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    // Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
