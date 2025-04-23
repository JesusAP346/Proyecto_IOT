package com.example.proyecto_iot.taxista;

public class Solicitud {
    public String nombre;
    public String telefono;
    public int viajes;
    public String tiempoDistancia;
    public String origen;
    public String distrito;
    public String destino;
    public int imagenPerfil; // puede ser un recurso drawable

    public Solicitud(String nombre, String telefono, int viajes, String tiempoDistancia,
                     String origen, String distrito, String destino, int imagenPerfil) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.viajes = viajes;
        this.tiempoDistancia = tiempoDistancia;
        this.origen = origen;
        this.distrito = distrito;
        this.destino = destino;
        this.imagenPerfil = imagenPerfil;
    }
}
