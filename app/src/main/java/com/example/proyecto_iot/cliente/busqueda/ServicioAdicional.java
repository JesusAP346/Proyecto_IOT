package com.example.proyecto_iot.cliente.busqueda;

import java.io.Serializable;
import java.util.List;

public class ServicioAdicional implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private List<String> fotosUrls;
    private boolean seleccionado;

    public ServicioAdicional() {
        // Constructor vacío requerido para Firebase
    }

    public ServicioAdicional(String id, String nombre, String descripcion, double precio, List<String> fotosUrls) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fotosUrls = fotosUrls;
        this.seleccionado = false;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public List<String> getFotosUrls() { return fotosUrls; }
    public void setFotosUrls(List<String> fotosUrls) { this.fotosUrls = fotosUrls; }

    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
}