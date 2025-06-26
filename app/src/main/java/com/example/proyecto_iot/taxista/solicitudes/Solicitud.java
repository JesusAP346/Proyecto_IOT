package com.example.proyecto_iot.taxista.solicitudes;
public class Solicitud {
    public String nombre, telefono, origen, distrito, destino, urlFotoPerfil;
    public int viajes;
    public String tiempoDistancia;
    public double latDestino, lngDestino;
    public String idDocumento; // ← NECESARIO para actualizar el documento

    public String estado;

    public Solicitud(String nombre, String telefono, int viajes, String tiempoDistancia,
                     String origen, String distrito, String destino,
                     String urlFotoPerfil, double latDestino, double lngDestino, String idDocumento, String estado) {
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
        this.idDocumento = idDocumento;
        this.estado = estado;
    }
    public Solicitud(String nombre, String telefono, int viajes, String tiempoDistancia,
                     String origen, String distrito, String destino,
                     String urlFotoPerfil, double latDestino, double lngDestino, String idDocumento) {
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
        this.idDocumento = idDocumento;

    }
}
