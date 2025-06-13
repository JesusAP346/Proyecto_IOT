package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

public class Auto {
    private String placa;
    private String color;
    private String modelo;

    public Auto(String placa, String color, String modelo) {
        this.placa = placa;
        this.color = color;
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getColor() {
        return color;
    }

    public String getModelo() {
        return modelo;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
