package com.tienda.tiendaropa.model;

public class ProveedorProducto {
    private String id;
    private String proveedorId;
    private String productoId;
    private int stock;
    private double precioCompra;

    public ProveedorProducto() {
    }

    public ProveedorProducto(String id, String proveedorId, String productoId, int stock, double precioCompra) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.productoId = productoId;
        this.stock = stock;
        this.precioCompra = precioCompra;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getProveedorId() {
        return proveedorId;
    }
    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProductoId() {
        return productoId;
    }
    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }
    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
}
