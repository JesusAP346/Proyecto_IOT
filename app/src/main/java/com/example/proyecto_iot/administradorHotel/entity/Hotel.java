package com.example.proyecto_iot.administradorHotel.entity;

import java.util.List;

public class Hotel {
    private String id;
    private String nombre;
    private String direccion;
    private List<String> referencias;
    private List<String> fotosHotelUrls;
    private String idAdministrador;

    public Hotel() {}

    public Hotel(String nombre, String direccion, List<String> referencias, List<String> fotosHotelUrls, String idAdministrador) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.referencias = referencias;
        this.fotosHotelUrls = fotosHotelUrls;
        this.idAdministrador = idAdministrador;
    }


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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<String> getReferencias() {
        return referencias;
    }

    public void setReferencias(List<String> referencias) {
        this.referencias = referencias;
    }

    public List<String> getFotosHotelUrls() {
        return fotosHotelUrls;
    }

    public void setFotosHotelUrls(List<String> fotosHotelUrls) {
        this.fotosHotelUrls = fotosHotelUrls;
    }

    public String getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(String idAdministrador) {
        this.idAdministrador = idAdministrador;
    }
}
