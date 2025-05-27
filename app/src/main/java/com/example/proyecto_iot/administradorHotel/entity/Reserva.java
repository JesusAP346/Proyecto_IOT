package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.List;

public class Reserva implements Serializable {
    public String nombreCompleto;
    public String dni;
    public String correo;
    public String telefono;
    public String tipoHabitacion;
    public String capacidad;
    public int tamanioM2;
    public List<String> equipamientos;
    public String checkIn;
    public String checkOut;
    public List<String> serviciosAdicionales;


    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getTipoHabitacion() {
        return tipoHabitacion;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public int getTamanioM2() {
        return tamanioM2;
    }

    public List<String> getEquipamientos() {
        return equipamientos;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public List<String> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public Reserva(String nombreCompleto, String dni, String correo, String telefono,
                   String tipoHabitacion, String capacidad, int tamanioM2,
                   List<String> equipamientos, String checkIn, String checkOut,
                   List<String> serviciosAdicionales) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoHabitacion = tipoHabitacion;
        this.capacidad = capacidad;
        this.tamanioM2 = tamanioM2;
        this.equipamientos = equipamientos;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.serviciosAdicionales = serviciosAdicionales;
    }
}
