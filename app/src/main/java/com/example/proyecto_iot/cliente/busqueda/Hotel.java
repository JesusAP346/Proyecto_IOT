package com.example.proyecto_iot.cliente.busqueda;

public class Hotel {
    private String nombre;
    private String ubicacion;
    private int precio;
    private int imagenResId;
    private int estrellas;

    public Hotel(String nombre, String ubicacion, int precio, int imagenResId, int estrellas) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.imagenResId = imagenResId;
        this.estrellas = estrellas;
    }

    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getPrecio() { return precio; }
    public int getImagenResId() { return imagenResId; }
    public int getEstrellas() { return estrellas; }
}
