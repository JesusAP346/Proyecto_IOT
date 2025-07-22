package com.example.proyecto_iot.administradorHotel.entity;

public class ReporteUsuario {
    private String idUsuario;
    private double montoTotal;

    public ReporteUsuario() {}

    public ReporteUsuario(String idUsuario, double montoTotal) {
        this.idUsuario = idUsuario;
        this.montoTotal = montoTotal;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }
}
