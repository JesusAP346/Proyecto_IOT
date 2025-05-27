package com.example.proyecto_iot.administradorHotel.dto;

import com.example.proyecto_iot.administradorHotel.entity.Habitacion;

import java.io.Serializable;
import java.util.List;

public class HabitacionDto {

    private int id;
    private String tipo;
    private int cantidadHabitaciones;
    private int fotoResId;

    public HabitacionDto(Habitacion habitacion) {
        this.id = habitacion.getId();
        this.tipo = habitacion.getTipo();
        this.cantidadHabitaciones = habitacion.getCantidadHabitaciones();
        List<Integer> fotos = habitacion.getFotosResIds();
        this.fotoResId = (fotos != null && !fotos.isEmpty()) ? fotos.get(0) : 0;
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public int getCantidadHabitaciones() { return cantidadHabitaciones; }
    public int getFotoResId() { return fotoResId; }
}
