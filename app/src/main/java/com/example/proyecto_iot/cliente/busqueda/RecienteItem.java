package com.example.proyecto_iot.cliente.busqueda;

public class RecienteItem {
    public String nombre;
    public String ubicacion;
    public String fechas;
    public int imagenResId;

    public RecienteItem(String nombre, String ubicacion, String fechas, int imagenResId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fechas = fechas;
        this.imagenResId = imagenResId;
    }
}
