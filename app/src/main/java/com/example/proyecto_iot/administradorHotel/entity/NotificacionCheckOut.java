package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.Date;

public class NotificacionCheckOut implements Serializable {
    private String nombreHuesped;
    private Date fecha;

    public NotificacionCheckOut(String nombreHuesped, Date fecha) {
        this.nombreHuesped = nombreHuesped;
        this.fecha = fecha;
    }

    public String getNombreHuesped() {
        return nombreHuesped;
    }

    public Date getFecha() {
        return fecha;
    }
}
