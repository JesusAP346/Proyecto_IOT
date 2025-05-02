package com.example.proyecto_iot.cliente.busqueda;

public class Habitacion {
    private String precio;
    private String etiqueta;
    private String descripcion;

    public Habitacion(String precio, String etiqueta, String descripcion) {
        this.precio = precio;
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

