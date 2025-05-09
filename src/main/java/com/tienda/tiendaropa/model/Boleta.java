package com.tienda.tiendaropa.model;

import java.util.List;

public class Boleta {
    private String id;
    private String clienteId;
    private List<DetalleBoleta> detalles;
    private double total;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getClienteId() {
        return clienteId;
    }
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    public List<DetalleBoleta> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetalleBoleta> detalles) {
        this.detalles = detalles;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }

}
