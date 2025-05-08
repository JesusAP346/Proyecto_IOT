package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.List;

public class Servicio implements Serializable {

    private String nombre;
    private String descripcion;
    private int precio;

    private List<Integer> fotosServicioIds;

    public Servicio(String nombre, String descripcion, int precio, List<Integer> fotosServicioIds) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fotosServicioIds = fotosServicioIds;

    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public List<Integer> getFotosServicioIds() {
        return fotosServicioIds;
    }
}
