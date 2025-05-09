package com.tienda.tiendaropa.model;

import java.util.List;

public class Carrito {
    private String clienteId;
    private List<ItemCarrito> productos;

    // Getters y Setters
    public String getClienteId() {
        return clienteId;
    }
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    public List<ItemCarrito> getProductos() {
        return productos;
    }
    public void setProductos(List<ItemCarrito> productos) {
        this.productos = productos;
    }

    // Clase interna para representar un ítem
    public static class ItemCarrito {
        private String productoId;
        private int cantidad;

        public String getProductoId() {
            return productoId;
        }
        public void setProductoId(String productoId) {
            this.productoId = productoId;
        }

        public int getCantidad() {
            return cantidad;
        }
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }
}
