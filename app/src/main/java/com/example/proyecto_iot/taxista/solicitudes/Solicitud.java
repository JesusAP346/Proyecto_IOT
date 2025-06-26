package com.example.proyecto_iot.taxista.solicitudes;
public class Solicitud {
    public String nombre;
    public String telefono;
    public int viajes;
    public String tiempoDistancia;
    public String origen;
    public String distrito;
    public String destino;
    public int imagenPerfil;
    public double latDestino;
    public double lngDestino;

    public String urlFotoPerfil; // en lugar de int fotoPerfil




    public Solicitud(String nombre, String telefono, int viajes, String tiempoDistancia,
                     String origen, String distrito, String destino,  String urlFotoPerfil,
                     double latDestino, double lngDestino) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.viajes = viajes;
        this.tiempoDistancia = tiempoDistancia;
        this.origen = origen;
        this.distrito = distrito;
        this.destino = destino;
        this.urlFotoPerfil = urlFotoPerfil;
        this.latDestino = latDestino;
        this.lngDestino = lngDestino;
    }

    // Constructor alternativo para compatibilidad
    public Solicitud(String nombre, String telefono, int viajes, String tiempoDistancia,
                     String origen, String distrito, String destino, String imagenPerfil) {
        this(nombre, telefono, viajes, tiempoDistancia, origen, distrito, destino, imagenPerfil,
                -12.0464, -77.0428); // coordenadas reales por defecto
    }

}