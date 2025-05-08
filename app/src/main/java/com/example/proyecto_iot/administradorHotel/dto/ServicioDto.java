package com.example.proyecto_iot.administradorHotel.dto;

import com.example.proyecto_iot.administradorHotel.entity.Servicio;

public class ServicioDto {
    private String nombre;
    private int imagenResId;

    public ServicioDto(Servicio servicio) {
        this.nombre = servicio.getNombre();
        this.imagenResId = servicio.getFotosServicioIds().isEmpty() ? 0 : servicio.getFotosServicioIds().get(0);
    }

    public String getNombre() {
        return nombre;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}