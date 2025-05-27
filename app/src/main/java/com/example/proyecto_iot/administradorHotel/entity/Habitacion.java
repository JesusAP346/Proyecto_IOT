package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.List;

public class Habitacion implements Serializable {

    private int id;
    private String tipo;
    private int capacidadAdultos;
    private int capacidadNinos;
    private int tamanho;
    private int cantidadHabitaciones;
    private double precioPorNoche;
    private List<Equipamiento> equipamiento;
    private List<Servicio> servicio;
    private List<Integer> fotosResIds;

    public Habitacion(int id, String tipo, int capacidadAdultos, int capacidadNinos, int tamanho, int cantidadHabitaciones, double precioPorNoche, List<Equipamiento> equipamiento, List<Servicio> servicio, List<Integer> fotosResIds) {
        this.id = id;
        this.tipo = tipo;
        this.capacidadAdultos = capacidadAdultos;
        this.capacidadNinos = capacidadNinos;
        this.tamanho = tamanho;
        this.cantidadHabitaciones = cantidadHabitaciones;
        this.precioPorNoche = precioPorNoche;
        this.equipamiento = equipamiento;
        this.servicio = servicio;
        this.fotosResIds = fotosResIds;
    }

    public int getId() {
        return id;
    }

    public int getCapacidadAdultos() {
        return capacidadAdultos;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCapacidadNinos() {
        return capacidadNinos;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getCantidadHabitaciones() {
        return cantidadHabitaciones;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public List<Equipamiento> getEquipamiento() {
        return equipamiento;
    }

    public List<Servicio> getServicio() {
        return servicio;
    }

    public List<Integer> getFotosResIds() {
        return fotosResIds;
    }
}
