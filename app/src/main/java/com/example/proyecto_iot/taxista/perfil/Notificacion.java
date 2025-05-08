package com.example.proyecto_iot.taxista.perfil;

public class Notificacion {
    private String mensaje;
    private String hora;
    private int iconoResId;

    public Notificacion(String mensaje, String hora, int iconoResId) {
        this.mensaje = mensaje;
        this.hora = hora;
        this.iconoResId = iconoResId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getHora() {
        return hora;
    }

    public int getIconoResId() {
        return iconoResId;
    }
}