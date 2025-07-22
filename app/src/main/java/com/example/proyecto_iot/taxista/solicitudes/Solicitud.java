package com.example.proyecto_iot.taxista.solicitudes;

public class Solicitud {
    public String nombre, telefono, origen, direccionHotel, destino, urlFotoPerfil;
    public int viajes;
    public String tiempoEstimado;
    public double latDestino, lngDestino;
    public double latTaxista, lngTaxista;
    public String idDocumento;
    public String estado;

    public Solicitud(String nombre, String telefono, int viajes, String tiempoEstimado,
                     String origen, String direccionHotel, String destino,
                     String urlFotoPerfil, double latDestino, double lngDestino,
                     double latTaxista, double lngTaxista, String idDocumento, String estado) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.viajes = viajes;
        this.tiempoEstimado = tiempoEstimado;
        this.origen = origen;
        this.direccionHotel = direccionHotel;
        this.destino = destino;
        this.urlFotoPerfil = urlFotoPerfil;
        this.latDestino = latDestino;
        this.lngDestino = lngDestino;
        this.latTaxista = latTaxista;
        this.lngTaxista = lngTaxista;
        this.idDocumento = idDocumento;
        this.estado = estado;
    }
}
