package com.example.proyecto_iot.administradorHotel.dto;

import com.example.proyecto_iot.administradorHotel.entity.Reserva;

public class ReservaDto {
    private final String nombre;
    private final String tipo;
    private final String checkIn;
    private final String checkOut;

    public ReservaDto(Reserva r) {
        this.nombre = r.nombreCompleto;
        this.tipo = r.tipoHabitacion;
        this.checkIn = r.checkIn;
        this.checkOut = r.checkOut;
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
}
