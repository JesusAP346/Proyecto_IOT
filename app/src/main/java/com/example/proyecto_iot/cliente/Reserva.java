package com.example.proyecto_iot.cliente;

public class Reserva {
    private String nombreHotel;
    private String ubicacion;
    private String estado;
    private int imagen;

    public Reserva(String nombreHotel, String ubicacion, String estado, int imagen) {
        this.nombreHotel = nombreHotel;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.imagen = imagen;
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
}
