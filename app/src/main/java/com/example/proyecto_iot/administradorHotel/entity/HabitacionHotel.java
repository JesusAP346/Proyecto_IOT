package com.example.proyecto_iot.administradorHotel.entity;

import java.util.List;

public class HabitacionHotel {

    private String id;
    private String tipo;
    private int capacidadAdultos;
    private int capacidadNinos;
    private int tamanho;
    private double precioPorNoche;
    private int cantidadHabitaciones;
    private List<String> equipamiento;
    private List<Servicio> servicio;
    private List<String> fotosUrls;

    public HabitacionHotel() {}

    public HabitacionHotel(String tipo, int capacidadAdultos, int capacidadNinos, int tamanho, double precioPorNoche, int cantidadHabitaciones, List<String> equipamiento, List<Servicio> servicio, List<String> fotosUrls) {
        this.tipo = tipo;
        this.capacidadAdultos = capacidadAdultos;
        this.capacidadNinos = capacidadNinos;
        this.tamanho = tamanho;
        this.precioPorNoche = precioPorNoche;
        this.cantidadHabitaciones = cantidadHabitaciones;
        this.equipamiento = equipamiento;
        this.servicio = servicio;
        this.fotosUrls = fotosUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public int getCantidadHabitaciones() {
        return cantidadHabitaciones;
    }

    public void setCantidadHabitaciones(int cantidadHabitaciones) {
        this.cantidadHabitaciones = cantidadHabitaciones;
    }

    public List<String> getEquipamiento() {
        return equipamiento;
    }

    public void setEquipamiento(List<String> equipamiento) {
        this.equipamiento = equipamiento;
    }

    public List<Servicio> getServicio() {
        return servicio;
    }

    public void setServicio(List<Servicio> servicio) {
        this.servicio = servicio;
    }

    public List<String> getFotosUrls() {
        return fotosUrls;
    }

    public void setFotosUrls(List<String> fotosUrls) {
        this.fotosUrls = fotosUrls;
    }
}
