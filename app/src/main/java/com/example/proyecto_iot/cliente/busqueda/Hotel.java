package com.example.proyecto_iot.cliente.busqueda;

import java.util.List;

public class Hotel {
    private String nombre;
    private String ubicacion;
    private int precio;
    private int imagenResId;
    private int estrellas;
    private List<String> servicios;

    public Hotel(String nombre, String ubicacion, int precio, int imagenResId, int estrellas, List<String> servicios) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.imagenResId = imagenResId;
        this.estrellas = estrellas;
        this.servicios = servicios;
    }

    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getPrecio() { return precio; }
    public int getImagenResId() { return imagenResId; }
    public int getEstrellas() { return estrellas; }
    public List<String> getServicios() { return servicios; } // Getter

    public void setServicios(List<String> servicios) {
        this.servicios = servicios;
    }
}
