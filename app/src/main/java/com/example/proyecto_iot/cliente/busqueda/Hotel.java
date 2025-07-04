package com.example.proyecto_iot.cliente.busqueda;

import java.io.Serializable;
import java.util.List;

public class Hotel implements Serializable {

    private String id;
    private String nombre;
    private String ubicacion;
    private String precio;
    private String imagenResId;
    private int estrellas;
    private List<String> servicios;

    private boolean favorito;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(String imagenResId) {
        this.imagenResId = imagenResId;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public void setServicios(List<String> servicios) {
        this.servicios = servicios;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
