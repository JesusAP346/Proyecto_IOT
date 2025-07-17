package com.example.proyecto_iot.administradorHotel.entity;

import com.example.proyecto_iot.dtos.Usuario;

import java.io.Serializable;

public class ReservaCompletaHotel implements Serializable {

    private ReservaHotel reserva;
    private Usuario usuario;

    private HabitacionHotel habitacion;

    public ReservaCompletaHotel() {}

    public ReservaCompletaHotel(ReservaHotel reserva, Usuario usuario, HabitacionHotel habitacion) {
        this.reserva = reserva;
        this.usuario = usuario;
        this.habitacion = habitacion;
    }

    public ReservaHotel getReserva() {
        return reserva;
    }

    public void setReserva(ReservaHotel reserva) {
        this.reserva = reserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public HabitacionHotel getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(HabitacionHotel habitacion) {
        this.habitacion = habitacion;
    }
}
