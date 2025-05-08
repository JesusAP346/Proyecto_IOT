package com.example.proyecto_iot.cliente.busqueda;

import java.io.Serializable;

public class Habitacion implements Serializable {
    private String precio;
    private String etiqueta;
    private String descripcion;
    private int id;

    public Habitacion(String precio, String etiqueta, String descripcion) {
        this.precio = precio;
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public Habitacion(String precio, String etiqueta, String descripcion, int id) {
        this.precio = precio;
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
        this.id = id;
    }

    // Getters
    public String getPrecio() { return precio; }
    public String getEtiqueta() { return etiqueta; }
    public String getDescripcion() { return descripcion; }
    public int getId() { return id; }

    // Setters
    public void setPrecio(String precio) { this.precio = precio; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setId(int id) { this.id = id; }
}