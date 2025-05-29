package com.example.proyecto_iot.taxista.perfil;

public class NotificacionDTO {
    public String mensaje;
    public String hora;
    public int iconoResId;

    public NotificacionDTO() {}

    public NotificacionDTO(String mensaje, String hora, int iconoResId) {
        this.mensaje = mensaje;
        this.hora = hora;
        this.iconoResId = iconoResId;
    }
}
