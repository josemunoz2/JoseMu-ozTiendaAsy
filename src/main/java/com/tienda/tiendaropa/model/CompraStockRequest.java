package com.tienda.tiendaropa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CompraStockRequest {

    @NotBlank(message = "El ID del producto es obligatorio")
    private String productoId;

    @NotBlank(message = "El ID del proveedor es obligatorio")
    private String proveedorId;

    @Positive(message = "La cantidad debe ser mayor que cero")
    private int cantidad;

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
