package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;
import java.util.List;

public class DatosCheckout implements Serializable {

    private String tipoHabitacion;
    private Double precioHabitacionPorNNoches;
    private int nochesExtra;
    private double precioNochesExtra;
    private int cantNoches;
    private double precioPorNoche;

    private List<ItemCosto> consumos;
    private List<ItemCosto> cargos;
    private List<ItemCosto> serviciosAdicionales;
    private double subtotalSinIGV;
    private double igv;
    private double total;

    public DatosCheckout(String tipoHabitacion, Double precioHabitacionPorNNoches, int nochesExtra, double precioNochesExtra, int cantNoches, double precioPorNoche, List<ItemCosto> consumos, List<ItemCosto> cargos, List<ItemCosto> serviciosAdicionales, double subtotalSinIGV, double igv, double total) {
        this.tipoHabitacion = tipoHabitacion;
        this.precioHabitacionPorNNoches = precioHabitacionPorNNoches;
        this.nochesExtra = nochesExtra;
        this.precioNochesExtra = precioNochesExtra;
        this.cantNoches = cantNoches;
        this.precioPorNoche = precioPorNoche;
        this.consumos = consumos;
        this.cargos = cargos;
        this.serviciosAdicionales = serviciosAdicionales;
        this.subtotalSinIGV = subtotalSinIGV;
        this.igv = igv;
        this.total = total;
    }

    public String getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(String tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Double getPrecioHabitacionPorNNoches() {
        return precioHabitacionPorNNoches;
    }

    public void setPrecioHabitacionPorNNoches(Double precioHabitacionPorNNoches) {
        this.precioHabitacionPorNNoches = precioHabitacionPorNNoches;
    }

    public void setServiciosAdicionales(List<ItemCosto> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }

    public DatosCheckout() {
        // Constructor vacío requerido por Firestore
    }

    public int getCantNoches() {
        return cantNoches;
    }

    public void setCantNoches(int cantNoches) {
        this.cantNoches = cantNoches;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public int getNochesExtra() {
        return nochesExtra;
    }

    public void setNochesExtra(int nochesExtra) {
        this.nochesExtra = nochesExtra;
    }

    public double getPrecioNochesExtra() {
        return precioNochesExtra;
    }

    public void setPrecioNochesExtra(double precioNochesExtra) {
        this.precioNochesExtra = precioNochesExtra;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getSubtotalSinIGV() {
        return subtotalSinIGV;
    }

    public void setSubtotalSinIGV(double subtotalSinIGV) {
        this.subtotalSinIGV = subtotalSinIGV;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ItemCosto> getConsumos() {
        return consumos;
    }

    public void setConsumos(List<ItemCosto> consumos) {
        this.consumos = consumos;
    }

    public List<ItemCosto> getCargos() {
        return cargos;
    }

    public void setCargos(List<ItemCosto> cargos) {
        this.cargos = cargos;
    }

    public List<ItemCosto> getServiciosAdicionales() {
        return serviciosAdicionales;
    }
}
