package com.example.proyecto_iot.administradorHotel.entity;

public class NotificacionAdmin {

    private String mensaje;
    private String hora;

    public NotificacionAdmin() {
    }

    public NotificacionAdmin(String mensaje, String hora) {
        this.mensaje = mensaje;
        this.hora = hora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
