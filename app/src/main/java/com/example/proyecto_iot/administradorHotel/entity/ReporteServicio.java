package com.example.proyecto_iot.administradorHotel.entity;

public class ReporteServicio {
    private String nombre;
    private long cantidad;
    private double total;

    public ReporteServicio() {}

    public ReporteServicio(String nombre, long cantidad, double total) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getNombre() {
        return nombre;
    }

    public long getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
