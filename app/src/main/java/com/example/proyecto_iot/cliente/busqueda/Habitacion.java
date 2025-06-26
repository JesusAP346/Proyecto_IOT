package com.example.proyecto_iot.cliente.busqueda;

import java.io.Serializable;
import java.util.List;

public class Habitacion implements Serializable {

    private String id; // Identificador único para la habitación
    private double precio;
    private String etiqueta;
    private String descripcion;
    private int cantidadHabitaciones;
    private int capacidadAdultos;
    private int capacidadNinos;
    private List<String> equipamiento;

    public Habitacion() {
    }

    public Habitacion(String id, double precio, String etiqueta, String descripcion, int cantidadHabitaciones, int capacidadAdultos, int capacidadNinos, List<String> equipamiento) {
        this.id = id;
        this.precio = precio;
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
        this.cantidadHabitaciones = cantidadHabitaciones;
        this.capacidadAdultos = capacidadAdultos;
        this.capacidadNinos = capacidadNinos;
        this.equipamiento = equipamiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadHabitaciones() {
        return cantidadHabitaciones;
    }

    public void setCantidadHabitaciones(int cantidadHabitaciones) {
        this.cantidadHabitaciones = cantidadHabitaciones;
    }

    public int getCapacidadAdultos() {
        return capacidadAdultos;
    }

    public void setCapacidadAdultos(int capacidadAdultos) {
        this.capacidadAdultos = capacidadAdultos;
    }

    public int getCapacidadNinos() {
        return capacidadNinos;
    }

    public void setCapacidadNinos(int capacidadNinos) {
        this.capacidadNinos = capacidadNinos;
    }

    public List<String> getEquipamiento() {
        return equipamiento;
    }

    public void setEquipamiento(List<String> equipamiento) {
        this.equipamiento = equipamiento;
    }
}