package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;

public class ServicioAdicionalNombrePrecio implements Serializable {

    private String nombre;
    private Double precio;


    public ServicioAdicionalNombrePrecio(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
