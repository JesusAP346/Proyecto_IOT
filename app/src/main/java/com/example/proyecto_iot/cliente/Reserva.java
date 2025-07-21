package com.example.proyecto_iot.cliente;

public class Reserva {
    private String nombreHotel;
    private String ubicacion;
    private String estado;
    private int imagen;

    private String fechaEntrada;
    private String fechaSalida;
    private String monto;
    private String id;

    private String idHotel;
    private String idHabitacion;

    public String getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }

    public String getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(String idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getId() {
        return id;
    }
/*
    public Reserva(String nombreHotel, String ubicacion, String estado, int imagen) {
        this.nombreHotel = nombreHotel;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.imagen = imagen;
    }*/

    public Reserva(String nombreHotel, String ubicacion, String estado, int imagen, String fechaEntrada, String fechaSalida, String monto, String id, String idHotel, String idHabitacion) {
        this.nombreHotel = nombreHotel;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.imagen = imagen;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.monto = monto;
        this.id = id;
        this.idHotel = idHotel;
        this.idHabitacion = idHabitacion;
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public int getImagen() {
        return imagen;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public String getMonto() {
        return monto;
    }
}
