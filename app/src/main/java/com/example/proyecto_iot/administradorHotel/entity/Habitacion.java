package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.List;

public class Habitacion implements Serializable {

    private int id;
    private String tipo;
    private String capacidad;
    private int tamanho;
    private int cantidad;
    private List<Equipamiento> equipamiento;
    private List<Servicio> servicio;
    private List<Integer> fotosResIds;

    public Habitacion(int id, String tipo, String capacidad, int tamanho, int cantidad, List<Equipamiento> equipamiento, List<Integer> fotosResIds, List<Servicio> servicio) {
        this.id = id;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.tamanho = tamanho;
        this.cantidad = cantidad;
        this.equipamiento = equipamiento;
        this.fotosResIds = fotosResIds;
        this.servicio = servicio;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTamanho() {
        return tamanho;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public int getCantidad() {
        return cantidad;
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
