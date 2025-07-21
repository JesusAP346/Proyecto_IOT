package com.example.proyecto_iot.cliente.busqueda;

public class Valoracion {
    private String nombreUsuario;
    private int estrellas;
    private String observaciones;
    private String servicio;
    private String volveria;

    public Valoracion() {

    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getVolveria() {
        return volveria;
    }

    public void setVolveria(String volveria) {
        this.volveria = volveria;
    }
}