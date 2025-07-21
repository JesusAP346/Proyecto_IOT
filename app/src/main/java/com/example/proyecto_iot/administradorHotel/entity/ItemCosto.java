package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;

public class ItemCosto implements Serializable {

    private String nombre;
    private double precio;

    public ItemCosto() {
        // Constructor vacío necesario para Firestore
    }
    public ItemCosto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
